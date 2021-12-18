package dev.joxit.androidapp.audiorecorder.activity.service.recorder.manager

interface AudioHandlerListener {
  fun onAudioHandlingFinish()
  fun onStopRecordingRequested()
}
