package dev.joxit.androidapp.audiorecorder.media

import android.media.AudioRecord
import android.media.AudioRecord.RECORDSTATE_RECORDING
import android.os.*
import dev.joxit.androidapp.audiorecorder.entity.AudioFormatEntity
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.roundToInt
import kotlin.math.sqrt


class AudioRecorder {
  private lateinit var mAudioFormat: AudioFormatEntity
  private var mAudioRecord: AudioRecord? = null
  private var mBufferSizeInShort = 0
  private var mChannels: Short = 0
  private val mHandler = AudioHandler()
  private var mListener: AudioRecorderListener? = null
  private var mRecordingStartTime: Long = 0
  private var mRecordingThread: Thread? = null
  private var mRecordingTime: Long = 0
  private var mState = State.IDLE
  val volume = doubleArrayOf(SPL_THRESHOLD, SPL_THRESHOLD)

  companion object {
    private const val DEFAULT_BUFFER_INCREASE_FACTOR = 3
    const val LEFT_CHANNEL_INDEX = 0
    const val RECORDER_AUDIO_ENCODING_FORMAT = 2
    private const val RECORDER_AUDIO_SOURCE = 1
    const val RIGHT_CHANNEL_INDEX = 1
    const val SPL_THRESHOLD = 94.0
    private const val MESSAGE_DEINIT = 1
    private const val MESSAGE_ERROR = 6
    private const val MESSAGE_INIT = 0
    private const val MESSAGE_PAUSE = 3
    private const val MESSAGE_RESUME = 4
    private const val MESSAGE_START = 2
    private const val MESSAGE_STOP = 5
    private const val TAG = "AudioRecorder"
  }

  interface AudioRecorderListener {
    fun onAudioCaptureFinish()
    fun onAudioCaptureStart()
    fun onError()
    fun onInitRecord()
    fun onPacketRecorded(packets: ShortArray)
    fun onPauseRecord()
    fun onResumeRecord()
    fun onStartRecord()
    fun onStopRecord()
  }

  enum class State {
    IDLE, INITIALIZED, RECORDING, PAUSED, STOPPING, STOPPED, DEINITED, ERROR
  }

  fun init(audioFormat: AudioFormatEntity, s: Short) {
    mAudioFormat = audioFormat
    mChannels = s
    mHandler.sendInit()
  }

  fun deinit() {
    mHandler.sendDeinit()
  }

  fun start() {
    mHandler.sendStart()
  }

  fun stop() {
    val thread = mRecordingThread
    mRecordingThread = null
    thread?.interrupt()
    mHandler.sendStop()
  }

  fun pause() {
    mHandler.sendPause()
  }

  fun resume() {
    mHandler.sendResume()
  }

  val elapsedTime: Int
    get() = (((if (mAudioRecord == null || mState != State.RECORDING) 0 else SystemClock.elapsedRealtime() - mRecordingStartTime) + mRecordingTime) / 1000)
      .toFloat()
      .roundToInt()

  fun setAudioRecorderListener(audioRecorderListener: AudioRecorderListener?) {
    mListener = audioRecorderListener
  }

  private fun initRecorder() {
    val channels = if (mChannels.toInt() == 1) 16 else 12
    val minBufferSize = AudioRecord.getMinBufferSize(mAudioFormat.getSampleRate(), channels, 2)
    if (minBufferSize == -1 || minBufferSize == -2) {
      throw AudioRecorderException("Error while getting the minimum buffer size. AudioRecord cannot be initialized.")
    }
    configureRecorder(channels, minBufferSize)
    mBufferSizeInShort = minBufferSize / 2
  }

  @Throws(AudioRecorderException::class)
  private fun deinitRecorder() {
    val audioRecord = mAudioRecord
    if (audioRecord != null) {
      try {
        audioRecord.stop()
        mAudioRecord!!.release()
      } catch (e: IllegalStateException) {
        throw AudioRecorderException(e)
      }
    }
  }

  @Throws(AudioRecorderException::class)
  private fun startRecorder() {
    mRecordingStartTime = SystemClock.elapsedRealtime()
    try {
      captureAudioData()
    } catch (e: IllegalStateException) {
      throw AudioRecorderException(e)
    }
  }

  @Throws(AudioRecorderException::class)
  private fun stopRecorder() {
    val audioRecord = mAudioRecord
    if (audioRecord != null) {
      try {
        audioRecord.stop()
        mAudioRecord!!.release()
        mAudioRecord = null
      } catch (e: IllegalStateException) {
        throw AudioRecorderException(e)
      }
    }
  }

  @Throws(AudioRecorderException::class)
  private fun pauseRecorder() {
    mRecordingTime += SystemClock.elapsedRealtime() - mRecordingStartTime
    val audioRecord = mAudioRecord
    if (audioRecord != null) {
      try {
        audioRecord.stop()
        mAudioRecord!!.release()
        mAudioRecord = null
      } catch (e: IllegalStateException) {
        throw AudioRecorderException(e)
      }
    }
  }

  @Throws(AudioRecorderException::class)
  private fun resumeRecorder() {
    try {
      initRecorder()
      captureAudioData()
      mRecordingStartTime = SystemClock.elapsedRealtime()
    } catch (e: IllegalStateException) {
      throw AudioRecorderException(e)
    }
  }

  @Throws(AudioRecorderException::class)
  private fun configureRecorder(channels: Int, bufferSize: Int) {
    try {
      mAudioRecord =
        AudioRecord(RECORDER_AUDIO_SOURCE, mAudioFormat.getSampleRate(), channels, RECORDER_AUDIO_ENCODING_FORMAT, bufferSize * DEFAULT_BUFFER_INCREASE_FACTOR)
    } catch (e: IllegalStateException) {
      throw AudioRecorderException(e)
    }
  }

  @Throws(IllegalStateException::class)
  private fun captureAudioData() {
    val audioRecord = mAudioRecord
    if (audioRecord != null) {
      audioRecord.startRecording()
      mRecordingThread = Thread({
        Process.setThreadPriority(-16)
        try {
          captureAudio()
        } catch (e: AudioRecorderException) {
          e.printStackTrace()
        }
      }, "AudioCapture Thread")
      mRecordingThread!!.start()
    }
  }

  @Throws(AudioRecorderException::class)
  private fun captureAudio() {
    val buffer = ShortArray(mBufferSizeInShort)
    var bufferSize: Int = mAudioFormat.getBufferSize()
    if (bufferSize <= 0) {
      bufferSize = mBufferSizeInShort
    }
    mListener!!.onAudioCaptureStart()
    while (mAudioRecord != null && mAudioRecord!!.recordingState == RECORDSTATE_RECORDING && mState == State.RECORDING) {
      val read = mAudioRecord!!.read(buffer, 0, bufferSize)
      if (read > 0) {
        val audioPackets = ShortArray(read)
        System.arraycopy(buffer, 0, audioPackets, 0, read)
        mListener!!.onPacketRecorded(audioPackets)
        setVolume(audioPackets, mChannels, volume)
      }
    }
  }

  private fun setVolume(audioPackets: ShortArray?, channels: Short, volume: DoubleArray) {
    var left = SPL_THRESHOLD
    var right = SPL_THRESHOLD
    if (audioPackets != null && audioPackets.isNotEmpty()) {
      var leftCalc = 0.0
      var rightCalc = 0.0
      audioPackets.forEachIndexed { idx, packet ->
        if (idx % channels == 0) {
          leftCalc += (packet * packet).toDouble()
        } else {
          rightCalc += (packet * packet).toDouble()
        }
      }
      val length = (audioPackets.size / channels).toDouble()
      leftCalc = sqrt(leftCalc / length)
      rightCalc = sqrt(rightCalc / length)
      left = if (leftCalc > 0.0) abs(log10(leftCalc / 32767.0) * 20.0) else SPL_THRESHOLD
      if (rightCalc > 0.0) {
        right = abs(log10(rightCalc / 32767.0) * 20.0)
      }
    }
    volume[LEFT_CHANNEL_INDEX] = left
    volume[RIGHT_CHANNEL_INDEX] = right
  }

  fun onAudioHandlingFinish() {
    mState = State.STOPPED
    val audioRecorderListener = mListener
    audioRecorderListener?.onStopRecord()
  }

  inner class AudioHandler : Handler.Callback {
    private val mLooper: Looper = HandlerThread(Companion.TAG).let {
      it.start()
      it.looper
    }
    private val mHandler: Handler = Handler(mLooper, this)

    init {
      mState = State.IDLE
    }

    override fun handleMessage(message: Message): Boolean {
      return when (message.what) {
        MESSAGE_INIT -> {
          if (mState == State.IDLE) {
            try {
              initRecorder()
              mState = State.INITIALIZED
              mListener?.onInitRecord() ?: return false
            } catch (unused: AudioRecorderException) {
              onError()
            }
          }
          false
        }
        MESSAGE_DEINIT -> {
          if (mState == State.STOPPED || mState == State.RECORDING) {
            try {
              deinitRecorder()
              mState = State.DEINITED
            } catch (_: AudioRecorderException) {
              onError()
            }
          }
          mLooper.quit()
          false
        }
        MESSAGE_START -> {
          if (mState == State.INITIALIZED) {
            try {
              startRecorder()
              mState = State.RECORDING
              mListener?.onStartRecord() ?: return false
            } catch (_: AudioRecorderException) {
              onError()
            }
          }
          false
        }
        MESSAGE_PAUSE -> {
          if (mState == State.RECORDING) {
            try {
              mState = State.PAUSED
              pauseRecorder()
              mListener?.onPauseRecord() ?: return false
            } catch (_: AudioRecorderException) {
              onError()
            }
          }
          false
        }
        MESSAGE_RESUME -> {
          if (mState == State.PAUSED || mState == State.ERROR) {
            try {
              resumeRecorder()
              mState = State.RECORDING
              mListener?.onResumeRecord() ?: return false
            } catch (_: AudioRecorderException) {
              onError()
            }
          }
          false
        }
        MESSAGE_STOP -> {
          if (mState != State.PAUSED && mState != State.RECORDING) {
            return false
          }
          val state = mState
          mState = State.STOPPING
          return try {
            stopRecorder()
            if (state != State.PAUSED || mListener == null) {
              return false
            }
            mListener!!.onAudioCaptureFinish()
            false
          } catch (_: AudioRecorderException) {
            onError()
            false
          }
        }
        MESSAGE_ERROR -> {
          onError()
          false
        }
        else -> false
      }
    }

    private fun removeAllMessages() {
      mHandler.removeMessages(MESSAGE_INIT)
      mHandler.removeMessages(MESSAGE_START)
      mHandler.removeMessages(MESSAGE_PAUSE)
      mHandler.removeMessages(MESSAGE_RESUME)
      mHandler.removeMessages(MESSAGE_STOP)
      mHandler.removeMessages(MESSAGE_ERROR)
    }

    fun sendInit() {
      sendSetupMessageCommand(MESSAGE_INIT)
    }

    fun sendDeinit() {
      sendSetupMessageCommand(MESSAGE_DEINIT)
    }

    fun sendStart() {
      sendMessageCommand(MESSAGE_START)
    }

    fun sendPause() {
      sendMessageCommand(MESSAGE_PAUSE)
    }

    fun sendResume() {
      sendMessageCommand(MESSAGE_RESUME)
    }

    fun sendStop() {
      sendMessageCommand(MESSAGE_STOP)
    }

    fun sendError() {
      sendMessageCommand(MESSAGE_ERROR)
    }

    private fun onError() {
      mState = State.ERROR
      mListener!!.onError()
    }

    private fun sendMessageCommand(i: Int) {
      mHandler.removeMessages(i)
      mHandler.sendEmptyMessage(i)
    }

    private fun sendSetupMessageCommand(i: Int) {
      removeAllMessages()
      mHandler.sendEmptyMessage(i)
    }
  }

  open class AudioRecorderException : Exception {
    constructor(msg: String) : super(msg)
    constructor(ex: Throwable?) : super(ex)
    constructor(msg: String?, ex: Throwable?) : super(msg, ex)
  }
}
