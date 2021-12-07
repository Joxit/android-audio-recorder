package dev.joxit.androidapp.audiorecorder.activity.permission.command

import android.os.Parcel
import android.os.Parcelable
import dev.joxit.androidapp.audiorecorder.entity.AudioFormatEntity

class StartRecord(
  private val channels: Short,
  private var maxFileSize: Long,
  private val audioFormat: AudioFormatEntity
) : PermissionCommand() {
  constructor(parcel: Parcel) : this(
    channels = parcel.readInt().toShort(),
    maxFileSize = parcel.readLong(),
    audioFormat = AudioFormatEntity.values()[parcel.readInt()]
  )

  override fun doInBackground() {
    if (maxFileSize <= 0) {
      maxFileSize = audioFormat.getMaxFileSize()
    }
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeInt(channels.toInt())
    dest.writeLong(maxFileSize)
    dest.writeInt(audioFormat.ordinal)
  }

  companion object CREATOR : Parcelable.Creator<StartRecord> {
    override fun createFromParcel(parcel: Parcel): StartRecord {
      return StartRecord(parcel)
    }

    override fun newArray(size: Int): Array<StartRecord?> {
      return arrayOfNulls(size)
    }
  }

}