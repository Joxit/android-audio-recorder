package dev.joxit.androidapp.audiorecorder.activity.recorder.state

import android.view.View
import dev.joxit.androidapp.audiorecorder.activity.ViewHolder
import dev.joxit.androidapp.audiorecorder.utils.TimerFormatter

enum class RecorderState {
  BLOCKED, ERROR, FINISHED, PAUSED, RECORDING, STOPPED;

  fun updateUI(recorderViewHolder: ViewHolder) {
    recorderViewHolder.micIcon.isEnabled = this == PAUSED || this == RECORDING

    recorderViewHolder.recordButton.isEnabled =
      this == FINISHED || this == PAUSED || this == STOPPED
    recorderViewHolder.recordButton.visibility =
      if (this == PAUSED || this == RECORDING) View.GONE else View.VISIBLE

    recorderViewHolder.stopButton.isEnabled = this == PAUSED || this == RECORDING
    recorderViewHolder.stopButton.isFocusable = this == PAUSED || this == RECORDING
    recorderViewHolder.stopButton.visibility = View.VISIBLE

    recorderViewHolder.pauseButton.isEnabled = this == RECORDING
    recorderViewHolder.pauseButton.visibility = if (this == RECORDING) View.VISIBLE else View.GONE

    recorderViewHolder.resumeButton.isEnabled = this == PAUSED
    recorderViewHolder.resumeButton.visibility = if (this == PAUSED) View.VISIBLE else View.GONE

    recorderViewHolder.recordingTime.isActivated = this == PAUSED || this == RECORDING
    if (this == STOPPED || this == FINISHED || this == BLOCKED || this == ERROR) {
      recorderViewHolder.recordingTime.text = TimerFormatter.formatInterval(0)
    } else if (this == PAUSED) {
      recorderViewHolder.recordingTime.text = TimerFormatter.formatInterval(/* time * */1000)
    }
  }
}