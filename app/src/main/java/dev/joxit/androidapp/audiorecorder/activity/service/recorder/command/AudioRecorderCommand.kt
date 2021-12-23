package dev.joxit.androidapp.audiorecorder.activity.service.recorder.command

import android.content.Context
import android.content.Intent
import android.os.Parcel
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.AudioRecorderService
import dev.joxit.androidapp.audiorecorder.common.command.IntentCommand

abstract class AudioRecorderCommand : IntentCommand() {
  abstract fun executeCommand(audioRecorderService: AudioRecorderService)
  override fun writeToParcel(parcel: Parcel, flags: Int) {}

  override fun createIntent(context: Context): Intent {
    return Intent(context, AudioRecorderService::class.java)
  }

  override fun execute(context: Context) {
    executeCommand(context as AudioRecorderService)
  }
}
