package dev.joxit.androidapp.audiorecorder

import android.app.Application

class AuReApp : Application() {
  companion object {
    private lateinit var sAppContext: AuReApp;

    private fun setContext(auReApp: AuReApp) {
      sAppContext = auReApp
    }

    fun getContext() = sAppContext
  }

  init {
    setContext(this)
  }
}