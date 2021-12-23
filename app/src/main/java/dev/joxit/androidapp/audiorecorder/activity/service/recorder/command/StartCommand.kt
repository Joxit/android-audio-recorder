package dev.joxit.androidapp.audiorecorder.activity.service.recorder.command

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.AudioRecorderService
import dev.joxit.androidapp.audiorecorder.activity.service.recorder.manager.RecorderManager
import dev.joxit.androidapp.audiorecorder.model.Entry


class StartCommand : AudioRecorderCommand {
  private val mEntry: Entry
  private val mManager: RecorderManager

  constructor(entry: Entry, recorderManager: RecorderManager) {
    mEntry = entry
    mManager = recorderManager
  }

  override fun executeCommand(audioRecorderService: AudioRecorderService) {
    audioRecorderService.start(mEntry, mManager)
  }

  constructor(parcel: Parcel) {
    mEntry = parcel.readParcelable(Entry::class.java.classLoader)!!
    mManager = parcel.readParcelable(RecorderManager::class.java.classLoader)!!
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    super.writeToParcel(parcel, flags)
    parcel.writeParcelable(mEntry, flags)
    parcel.writeParcelable(mManager, flags)
  }

  companion object CREATOR : Parcelable.Creator<StartCommand> {
    override fun createFromParcel(parcel: Parcel): StartCommand {
      return StartCommand(parcel)
    }

    override fun newArray(size: Int): Array<StartCommand?> {
      return arrayOfNulls(size)
    }
  }
}