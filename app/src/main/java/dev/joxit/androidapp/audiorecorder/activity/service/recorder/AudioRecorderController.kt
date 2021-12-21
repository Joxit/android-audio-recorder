package dev.joxit.androidapp.audiorecorder.activity.service.recorder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Handler
import android.os.PowerManager
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import android.os.PowerManager.WakeLock
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.exception.LowSpaceException
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.exception.TelephonyException
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.manager.AudioHandlerListener
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.manager.RecorderManager
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.receiver.CallReceiver
import dev.joxit.androidapp.audiorecorder.common.command.CommandQueue
import dev.joxit.androidapp.audiorecorder.media.AudioRecorder
import dev.joxit.androidapp.audiorecorder.media.VolumeControl
import dev.joxit.androidapp.audiorecorder.model.Entry
import dev.joxit.androidapp.audiorecorder.model.resource.RecorderStatus
import java.util.concurrent.TimeUnit

class AudioRecorderController(
  private val mContext: Context,
  private val mListener: RecorderStateListener
) :
  AudioRecorder.AudioRecorderListener,
  VolumeControl.VolumeCallback,
  AudioHandlerListener,
  CallReceiver.CallReceiverListener {
  private var mCallReceiver: CallReceiver? = null
  private var mCurrentAudio: Entry? = null
  private var mPauseReasonWasOffHook = false
  private var mRecorder: AudioRecorder? = null
  private val mRecorderLock = Any()
  private var mRecorderManager: RecorderManager? = null
  private val mStateLock = Any()
  private var mStatus: RecorderStatus = RecorderStatus.STOPPED
  private val mUserSwitchReceiver: UserSwitchReceiver = UserSwitchReceiver()
  private lateinit var mVolumeControl: VolumeControl
  private val mVolumeControlLock = Any()
  private var mWakeLock: WakeLock? = null

  companion object {
    private const val PHONE_ACTION = "android.intent.action.PHONE_STATE"
  }

  interface RecorderStateListener {
    fun onRecorderIdle()
    fun onRecorderReleased()
  }

  override fun playAudio() {}
  fun onCreate() {
    mUserSwitchReceiver.register(mContext)
    mVolumeControl = VolumeControl(inferGainType(), mContext, this)
    mVolumeControl.onCreate()
  }

  private fun inferGainType(): Int {
    val audioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    return try {
      if (!audioManager.isBluetoothA2dpOn || !audioManager.isWiredHeadsetOn) {
        1
      } else 4
    } catch (_: NullPointerException) {
      1
    }
  }

  fun onDestroy() {
    freeResources()
    updateStatus(RecorderStatus.STOPPED)
    mRecorderManager?.onDestroy()
    mUserSwitchReceiver.unregister(mContext)
  }

  fun onTaskRemoved() {
    freeResources()
  }

  val status: RecorderStatus
    get() = mStatus

  @Synchronized
  fun stop() {
    synchronized(mStateLock) {
      if ((mStatus === RecorderStatus.PAUSED) || (mStatus === RecorderStatus.RECORDING) || (mStatus === RecorderStatus.INITIALIZING)) {
        synchronized(mVolumeControlLock) {
          mVolumeControl.onDestroy()
        }
        synchronized(mRecorderLock) {
          mRecorder?.stop()
        }
      }
    }
  }

  @Synchronized
  fun start(entry: Entry, recorderManager: RecorderManager?) {
    synchronized(mStateLock) {
      if (mStatus === RecorderStatus.STOPPED || mStatus === RecorderStatus.ERROR) {
        mRecorderManager = recorderManager
        startRecording(entry)
      }
    }
  }

  private fun startRecording(entry: Entry) {
    synchronized(mStateLock) {
      if (mStatus === RecorderStatus.STOPPED) {
        updateStatus(RecorderStatus.INITIALIZING)
        mCurrentAudio = entry
        mRecorderManager?.onEntryChanged(mCurrentAudio!!)
        initRecord()
      }
    }
  }

  @Synchronized
  private fun initRecord() {
    if (mStatus !== RecorderStatus.INITIALIZING) {
      freeResources()
      return
    }
    initResources()
    synchronized(mRecorderLock) {
      synchronized(mVolumeControlLock) {
        mVolumeControl.requestAudioFocus()

      }
      try {
        requestAudioFocus()
        mRecorderManager?.prepare(mContext.applicationContext, mCurrentAudio!!)
        mRecorder?.init(mCurrentAudio!!.format, mCurrentAudio!!.channels)
        mRecorder?.start()
      } catch (_: TelephonyException) {
        releaseRecord()
      } catch (_: LowSpaceException) {
        releaseRecord()
      } catch (_: AudioRecorder.AudioRecorderException) {
        onError()
      }
    }
  }

  @Synchronized
  fun pause() {
    synchronized(mStateLock) {
      if (mStatus === RecorderStatus.RECORDING) {
        synchronized(mRecorderLock) { mRecorder?.pause() }
        try {
          mRecorderManager?.onRecordingPaused()
        } catch (_: AudioRecorder.AudioRecorderException) {
          onError()
        }
      }
    }
  }

  @Synchronized
  fun resume() {
    synchronized(mStateLock) {
      if (mStatus === RecorderStatus.PAUSED) {
        try {
          requestAudioFocus()
          mRecorderManager?.prepare(
            mContext.applicationContext,
            mCurrentAudio!!
          )
          resumeRecording()
        } catch (_: AudioRecorder.AudioRecorderException) {
          stop()
        }
      }
    }
  }

  private fun requestAudioFocus() {
    val handler = Handler()
    val afChangeListener = OnAudioFocusChangeListener { focusChange ->
      if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
        pause()
        handler.postDelayed({ stop() }, TimeUnit.SECONDS.toMillis(30))
      }
    }
    val audioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.requestAudioFocus(
      AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        )
        .setAcceptsDelayedFocusGain(true)
        .setOnAudioFocusChangeListener(afChangeListener, handler)
        .build()
    )
  }

  private fun resumeRecording() {
    synchronized(mVolumeControlLock) {
      mVolumeControl.requestAudioFocus()

    }
    synchronized(mRecorderLock) {
      mRecorder?.resume()
    }
  }

  override fun pauseAudio() {
    val callReceiver: CallReceiver? = mCallReceiver
    if (callReceiver == null || !callReceiver.isOngoingCall) {
      pause()
    }
  }

  override val isPlaying: Boolean
    get() = mStatus === RecorderStatus.RECORDING

  override fun setVolume(volume: Float) {
    if (volume == 0.0f) {
      mRecorder!!.pause()
    } else if (mStatus === RecorderStatus.PAUSED) {
      mRecorder!!.resume()
    }
  }

  override fun onAudioCaptureStart() {
    try {
      mRecorderManager!!.startRecording()
    } catch (_: AudioRecorder.AudioRecorderException) {
      onError()
    }
  }

  @Throws(AudioRecorder.AudioRecorderException::class)
  override
  fun onPacketRecorded(packets: ShortArray) {
    mRecorderManager!!.onPacketReceived(packets)
  }


  @Synchronized
  override
  fun onAudioCaptureFinish() {
    println("AudioRecorderController.onAudioCaptureFinish")
    try {
      mRecorderManager?.finishRecording()
    } catch (e: AudioRecorder.AudioRecorderException) {
      e.printStackTrace()
    }
    onError()
    return
  }

  override fun onStartRecord() {
    synchronized(mStateLock) { updateStatus(RecorderStatus.RECORDING) }
  }

  override fun onPauseRecord() {
    synchronized(mVolumeControlLock) { mVolumeControl.abandonAudioFocus() }
    synchronized(mStateLock) { updateStatus(RecorderStatus.PAUSED) }
    mListener.onRecorderIdle()
  }

  override fun onResumeRecord() {
    synchronized(mStateLock) { updateStatus(RecorderStatus.RECORDING) }
  }

  override fun onStopRecord() {
    releaseRecord()
  }

  private fun releaseRecord() {
    synchronized(mStateLock) { updateStatus(RecorderStatus.FINALIZING) }
    freeResources()
  }

  override fun onError() {
    synchronized(mStateLock) {
      if (mStatus !== RecorderStatus.PAUSED) {
        stop()
        updateStatus(RecorderStatus.ERROR)
      }
    }
    releaseRecord()
  }

  override fun onInitRecord() {
    try {
      mRecorderManager!!.prepareRecording(mContext.applicationContext, this, mCurrentAudio!!)
    } catch (_: AudioRecorder.AudioRecorderException) {
      onError()
    }
  }

  val elapsedTime: Int
    get() = try {
      mRecorder!!.elapsedTime
    } catch (_: Exception) {
      0
    }
  val volume: DoubleArray?
    get() {
      return try {
        mRecorder!!.volume
      } catch (_: Exception) {
        null
      }
    }

  override fun onStopRecordingRequested() {
    synchronized(mRecorderLock) { mRecorder?.stop() }
  }

  override fun onAudioHandlingFinish() {
    if (mRecorder != null) {
      synchronized(mStateLock) {
        if ((mStatus === RecorderStatus.RECORDING) || (mStatus === RecorderStatus.PAUSED) || (mStatus === RecorderStatus.STOPPED)) {
          synchronized(mRecorderLock) { mRecorder!!.onAudioHandlingFinish() }
        }
      }
    }
  }

  @Synchronized
  private fun initResources() {
    if (mCallReceiver == null) {
      val intentFilter = IntentFilter(PHONE_ACTION)
      mCallReceiver = CallReceiver()
      mCallReceiver!!.setListener(this)
      mContext.registerReceiver(mCallReceiver, intentFilter)
    }
    if (mWakeLock == null) {
      try {
        mWakeLock = (mContext.getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
          PARTIAL_WAKE_LOCK,
          javaClass.name
        )
        mWakeLock!!.setReferenceCounted(false)
        mWakeLock!!.acquire()
      } catch (_: NullPointerException) {
        return
      }
    }
    synchronized(mRecorderLock) {
      if (mRecorder == null) {
        mRecorder = AudioRecorder()
        mRecorder!!.setAudioRecorderListener(this)
      }
    }
  }

  @Synchronized
  private fun freeResources() {
    mRecorderManager?.let {
      try {
        it.finishRecording()
        it.release()
      } catch (e: AudioRecorder.AudioRecorderException) {
        e.printStackTrace()
      }
    }
    mVolumeControl.onDestroy()
    synchronized(mRecorderLock) {
      mRecorder = mRecorder?.let {
        it.stop()
        it.deinit()
        null
      }
    }
    mWakeLock = mWakeLock?.let {
      it.release()
      null
    }
    mCallReceiver = mCallReceiver?.let {
      it.setListener(null)
      mContext.unregisterReceiver(it)
      null
    }
    updateStatus(RecorderStatus.FINALIZING)
    mListener.onRecorderReleased()
  }

  private fun updateStatus(recorderStatus: RecorderStatus) {
    mStatus = recorderStatus
    mRecorderManager?.onStatusChanged(recorderStatus, mContext)
  }

  override fun onIdleState() {
    if (!mPauseReasonWasOffHook || mStatus !== RecorderStatus.PAUSED) {
      updateStatus(mStatus)
      return
    }
    mPauseReasonWasOffHook = false
    CommandQueue(mContext, false, null).add(object : CommandQueue.Command {
      override fun onFinish(z: Boolean) {}

      override fun run(): Boolean {
        resume()
        return true
      }
    })
  }

  override fun onOffHookState() {
    pause()
    mPauseReasonWasOffHook = true
  }

  inner class UserSwitchReceiver : BroadcastReceiver() {
    private var mRegistered = false
    fun register(context: Context) {
      if (!mRegistered) {
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.USER_BACKGROUND")
        intentFilter.addAction("android.intent.action.USER_FOREGROUND")
        context.registerReceiver(this, intentFilter)
        mRegistered = true
      }
    }

    fun unregister(context: Context) {
      if (mRegistered) {
        context.unregisterReceiver(this)
        mRegistered = false
      }
    }

    override fun onReceive(context: Context, intent: Intent) {
      mRecorderManager?.onStatusChanged(RecorderStatus.STOPPED, context)
      stop()
    }
  }
}
