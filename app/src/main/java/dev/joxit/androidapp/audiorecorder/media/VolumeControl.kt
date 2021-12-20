package dev.joxit.androidapp.audiorecorder.media

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.AudioManager.*
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message


class VolumeControl(i: Int, context: Context, volumeCallback: VolumeCallback) :
  OnAudioFocusChangeListener {
  private val mAudioManager: AudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
  private val mCallback: VolumeCallback = volumeCallback
  private var mCurrentVolume = 1.0f
  private var mFocusGained: Boolean = false
  private val mFocusLock: Any = Any()
  private val mGainType: Int = i
  private lateinit var mHandler: Handler
  private var mLooper: Looper? = null
  private var mTransientLossOfFocus = false

  companion object {
    private const val FADEDOWN = 2
    private const val FADEUP = 3
    private const val FOCUSCHANGE = 1
    private const val TAG = "VolumeControl"
  }

  interface VolumeCallback {
    val isPlaying: Boolean
    fun pauseAudio()
    fun playAudio()
    fun setVolume(volume: Float)
  }

  fun onCreate() {
    object : HandlerThread(TAG) {
      public override fun onLooperPrepared() {
        super.onLooperPrepared()
        mLooper = looper
        val volumeControl = this@VolumeControl
        volumeControl.mHandler = Handler(volumeControl.mLooper, VolumeHandler())
      }
    }.start()
  }

  fun requestAudioFocus(): Boolean {
    return synchronized(mFocusLock) {
      if (!mFocusGained) {
        mFocusGained = mAudioManager
          .requestAudioFocus(this, FADEUP, mGainType) == AUDIOFOCUS_REQUEST_GRANTED
      }
      mFocusGained
    }
  }

  fun abandonAudioFocus() {
    synchronized(mFocusLock) {
      if (mFocusGained) {
        mAudioManager.abandonAudioFocus(this)
        mFocusGained = false
      }
    }
  }

  fun onDestroy() {
    abandonAudioFocus()
    mLooper?.let {
      it.quit()
      mLooper = null
    }
  }

  fun fadeUp() {
    mHandler.removeMessages(FADEDOWN)
    mHandler.sendEmptyMessage(FADEUP)
  }

  fun fadeDown() {
    mHandler.removeMessages(FADEUP)
    mHandler.sendEmptyMessage(FADEDOWN)
  }

  override fun onAudioFocusChange(focusChange: Int) {
    mHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget()
  }

  private inner class VolumeHandler : Handler.Callback {
    override fun handleMessage(message: Message): Boolean {
      when (message.what) {
        FOCUSCHANGE -> {
          val arg1 = message.arg1
          if (arg1 == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            fadeDown()
          } else if (arg1 == AUDIOFOCUS_LOSS_TRANSIENT) {
            if (mCallback.isPlaying) {
              mTransientLossOfFocus = true
            }
            mCallback.pauseAudio()
          } else if (arg1 == AUDIOFOCUS_LOSS) {
            if (mCallback.isPlaying) {
              mTransientLossOfFocus = false
            }
            mCallback.pauseAudio()
          } else if (arg1 == AUDIOFOCUS_GAIN) {
            if (mCallback.isPlaying || !mTransientLossOfFocus) {
              fadeUp()
            } else {
              mTransientLossOfFocus = false
              mCurrentVolume = 0.0f
              mCallback.setVolume(mCurrentVolume)
              mCallback.playAudio()
            }
          }
        }
        FADEDOWN -> {
          mCurrentVolume -= 0.05f
          if (mCurrentVolume > 0.2f) {
            mHandler.sendEmptyMessageDelayed(FADEDOWN, 10)
          } else {
            mCurrentVolume = 0.2f
          }
          mCallback.setVolume(mCurrentVolume)
        }
        FADEUP -> {
          mCurrentVolume += 0.01f
          if (mCurrentVolume < 1.0f) {
            mHandler.sendEmptyMessageDelayed(FADEUP, 10)
          } else {
            mCurrentVolume = 1.0f
          }
          mCallback.setVolume(mCurrentVolume)
        }
      }
      return true
    }
  }
}
