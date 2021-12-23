package dev.joxit.androidapp.audiorecorder.activity.service.recorder.command

import android.os.Parcel
import android.os.Parcelable
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.AudioRecorderService


class ResumeCommand() : AudioRecorderCommand() {
   override fun executeCommand(audioRecorderService: AudioRecorderService) {
    audioRecorderService.resume()
  }

  private constructor(parcel: Parcel): this()

  companion object CREATOR : Parcelable.Creator<ResumeCommand> {
    override fun createFromParcel(parcel: Parcel): ResumeCommand {
      return ResumeCommand(parcel)
    }

    override fun newArray(size: Int): Array<ResumeCommand?> {
      return arrayOfNulls(size)
    }
  }
}
