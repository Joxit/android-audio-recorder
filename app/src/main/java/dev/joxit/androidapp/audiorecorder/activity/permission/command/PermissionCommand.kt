package dev.joxit.androidapp.audiorecorder.activity.permission.command

import android.os.AsyncTask
import android.os.Parcel
import android.os.Parcelable

abstract class PermissionCommand() : Parcelable {
  override fun describeContents(): Int = 0

  abstract fun doInBackground()

  fun start() {
    Command().execute()
  }

  private inner class Command : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
      this@PermissionCommand.doInBackground()
      return null
    }

  }

}