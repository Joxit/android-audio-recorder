package dev.joxit.androidapp.audiorecorder

import android.app.Application

class AuReApp : Application() {
  companion object {
    lateinit var context: AuReApp
      private set
  }

  init {
    context = this
  }
}