package dev.joxit.androidapp.audiorecorder.activity

import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import dev.joxit.androidapp.audiorecorder.R

data class ViewHolder(
  val pauseButton: ImageButton,
  val recordButton: ImageButton,
  val resumeButton: ImageButton,
  val stopButton: ImageButton,
  val micIcon: ImageView,
  val recordingTime: TextView,
) {

  companion object {
    fun create(activity: MainActivity) = ViewHolder(
      pauseButton = activity.findViewById(R.id.pause_button),
      recordButton = activity.findViewById(R.id.record_button),
      resumeButton = activity.findViewById(R.id.resume_button),
      stopButton = activity.findViewById(R.id.stop_button),
      micIcon = activity.findViewById(R.id.microphone_image),
      recordingTime = activity.findViewById(R.id.recording_time)
    )
  }
}
