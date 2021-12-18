package dev.joxit.androidapp.audiorecorder.activity.service.recorder.manager

import android.content.Context
import android.os.Parcelable
import dev.joxit.androidapp.audiorecorder.model.Entry
import dev.joxit.androidapp.audiorecorder.model.resource.RecorderStatus

interface RecorderStatusHandler : Parcelable {
  fun onEntryChanged(entry: Entry)
  fun onStatusChanged(recorderStatus: RecorderStatus, context: Context)
}
