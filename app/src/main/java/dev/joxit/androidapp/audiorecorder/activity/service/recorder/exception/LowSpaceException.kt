package dev.joxit.androidapp.audiorecorder.activity.service.recorder.exception

import dev.joxit.androidapp.audiorecorder.media.AudioRecorder

class LowSpaceException : AudioRecorder.AudioRecorderException {
  constructor(msg: String) : super(msg)
  constructor(ex: Throwable) : super(ex)
  constructor(msg: String, ex: Throwable) : super(msg, ex) {}
}
