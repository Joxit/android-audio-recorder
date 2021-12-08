package dev.joxit.androidapp.audiorecorder.activity.service.recorder

import android.app.IntentService
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class AudioRecorderService : IntentService("AudioRecorderService") {
  private var broadcastManager: LocalBroadcastManager? = null
  private var isDistroyed = true
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
    isDistroyed = false
    sendBroadcast(SERVICE_CREATED_INTENT)
  }

  override fun onDestroy() {
    super.onDestroy()
    isDistroyed = true
    broadcastManager = null
  }

  override fun onBind(intent: Intent?): IBinder = binder

  private fun getElapsedTime(): Int {
    return this.controller?.getElapsedTime() ?: 0
  }

  private fun getVolume(): DoubleArray? {
    return this.controller?.getVolume()
  }

  inner class AudioRecorderBinder : Binder() {
    val volume: DoubleArray?
      get() = this@AudioRecorderService.getVolume()
    val elapsedTime: Int
      get() = this@AudioRecorderService.getElapsedTime()
//    val audioHandlerListener: AudioHandlerListener
//      get() = this@AudioRecorderService.controller
  }
}