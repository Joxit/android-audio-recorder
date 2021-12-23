package dev.joxit.androidapp.audiorecorder.activity.service.recorder.command

import android.os.Parcel
import android.os.Parcelable
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.AudioRecorderService

class StopCommand() : AudioRecorderCommand() {
  override fun executeCommand(audioRecorderService: AudioRecorderService) {
    audioRecorderService.stop()
  }

  constructor(parcel: Parcel) : this()

  companion object CREATOR : Parcelable.Creator<StopCommand> {
    override fun createFromParcel(parcel: Parcel): StopCommand {
      return StopCommand(parcel)
    }

    override fun newArray(size: Int): Array<StopCommand?> {
      return arrayOfNulls(size)
    }
  }
}
