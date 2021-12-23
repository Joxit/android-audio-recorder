package dev.joxit.androidapp.audiorecorder.activity.service.recorder.command

import android.os.Parcel
import android.os.Parcelable
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.AudioRecorderService

class PauseCommand() : AudioRecorderCommand() {
  override fun executeCommand(audioRecorderService: AudioRecorderService) {
    audioRecorderService.resume()
  }

  private constructor(parcel: Parcel): this()

  companion object CREATOR : Parcelable.Creator<PauseCommand> {
    override fun createFromParcel(parcel: Parcel): PauseCommand {
      return PauseCommand(parcel)
    }

    override fun newArray(size: Int): Array<PauseCommand?> {
      return arrayOfNulls(size)
    }
  }
}