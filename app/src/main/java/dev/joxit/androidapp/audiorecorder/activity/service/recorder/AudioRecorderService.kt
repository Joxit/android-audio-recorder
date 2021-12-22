package dev.joxit.androidapp.audiorecorder.activity.service.recorder

import android.app.IntentService
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.manager.AudioHandlerListener
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.manager.RecorderManager
import dev.joxit.androidapp.audiorecorder.model.Entry

class AudioRecorderService : IntentService("AudioRecorderService"),
  AudioRecorderController.RecorderStateListener {
  private var broadcastManager: LocalBroadcastManager? = null
  private var isDestroyed = true
  private var controller: AudioRecorderController? = null
  private val binder: IBinder = AudioRecorderBinder()

  companion object {
    private val SERVICE_CREATED_INTENT =
      Intent("dev.joxit.androidapp.audiorecorder.RECORDER_CREATED")
  }

  override fun onHandleIntent(intent: Intent) {
    ContextCompat.startForegroundService(this, intent)
  }

  override fun onCreate() {
    super.onCreate()
    broadcastManager = LocalBroadcastManager.getInstance(applicationContext)
    isDestroyed = false
    controller = AudioRecorderController(this, this)
    sendBroadcast(SERVICE_CREATED_INTENT)
  }

  override fun onDestroy() {
    super.onDestroy()
    isDestroyed = true
    broadcastManager = null
    controller?.onDestroy()
    controller = null
  }

  override fun onBind(intent: Intent?): IBinder = binder

  private fun getElapsedTime(): Int {
    return controller?.elapsedTime ?: 0
  }

  private fun getVolume(): DoubleArray? {
    return controller?.volume
  }

  override fun onTaskRemoved(intent: Intent?) {
    super.onTaskRemoved(intent)
    System.currentTimeMillis()
    controller?.onTaskRemoved()
  }

  @Synchronized
  fun stop() {
    controller?.let {
      if (!isDestroyed) {
        it.stop()
      } else {
        it.onDestroy()
      }
    }
  }

  @Synchronized
  fun start(entry: Entry, recorderManager: RecorderManager) {
    controller?.let {
      if (!isDestroyed) {
        it.start(entry, recorderManager)
      } else {
        it.stop()
      }
    }
  }

  @Synchronized
  fun pause() {
    controller?.let {
      if (isDestroyed) {
        it.pause()
      } else {
        it.stop()
      }
    }
  }

  @Synchronized
  fun resume() {
    controller?.let {
      if (isDestroyed) {
        it.resume()
      } else {
        it.stop()
      }
    }
  }
  override fun onRecorderIdle() {
    gotoIdleState()
  }

  private fun gotoIdleState() {}

  override fun onRecorderReleased() {
    if (!this.isDestroyed) {
      gotoIdleState()
      stopSelf()
    }
  }

  inner class AudioRecorderBinder : Binder() {
    val volume: DoubleArray?
      get() = this@AudioRecorderService.getVolume()
    val elapsedTime: Int
      get() = this@AudioRecorderService.getElapsedTime()
    val audioHandlerListener: AudioHandlerListener
      get() = this@AudioRecorderService.controller!!
  }
}