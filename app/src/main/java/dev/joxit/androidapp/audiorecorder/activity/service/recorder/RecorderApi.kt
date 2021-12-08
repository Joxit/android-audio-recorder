package dev.joxit.androidapp.audiorecorder.activity.service.recorder

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import java.util.concurrent.atomic.AtomicBoolean


class RecorderApi(private val context: Context) : ServiceConnection {
  @Volatile
  private var mBinder: AudioRecorderService.AudioRecorderBinder? = null
  private val enabled = AtomicBoolean(false)

  override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
    this.mBinder = service as AudioRecorderService.AudioRecorderBinder
    this.enabled.set(true)
  }

  override fun onServiceDisconnected(name: ComponentName?) {
    this.enabled.set(false)
    this.mBinder = null
  }

}
