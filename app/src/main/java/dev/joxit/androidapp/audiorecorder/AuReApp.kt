package dev.joxit.androidapp.audiorecorder

import android.app.Application
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.RecorderApi

class AuReApp : Application() {
  companion object {
    lateinit var context: AuReApp
      private set
  }

  val audioRecorderApi by lazy { RecorderApi(context) }

  init {
    context = this
  }
}