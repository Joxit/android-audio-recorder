package dev.joxit.androidapp.audiorecorder.model.resource

enum class MicrophoneProvider(val dataServicePath: String) {
  MOBILE("/mobile"),
  WEAR("/wear"),
  MICROPHONE_TEST("/mic_test"),
  EUGENE("/mobile"),
  PICKER("/picker")
}