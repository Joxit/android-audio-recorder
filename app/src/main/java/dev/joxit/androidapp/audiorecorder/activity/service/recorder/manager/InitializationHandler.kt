package dev.joxit.androidapp.audiorecorder.activity.service.recorder.manager

import android.content.Context
import android.os.Parcelable
import dev.joxit.androidapp.audiorecorder.media.AudioRecorder
import dev.joxit.androidapp.audiorecorder.model.Entry

interface InitializationHandler : Parcelable {
  @Throws(AudioRecorder.AudioRecorderException::class)
  fun onPauseRecording()

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun onResumeRecording()

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun prepare(context: Context, entry: Entry)

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun release()
}
