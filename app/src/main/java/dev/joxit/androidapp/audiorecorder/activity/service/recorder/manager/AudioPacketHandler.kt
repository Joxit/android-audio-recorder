package dev.joxit.androidapp.audiorecorder.activity.service.recorder.manager

import android.content.Context
import android.os.Parcelable
import dev.joxit.androidapp.audiorecorder.media.AudioRecorder
import dev.joxit.androidapp.audiorecorder.model.Entry

interface AudioPacketHandler : Parcelable {
  @Throws(AudioRecorder.AudioRecorderException::class)
  fun finishRecording()

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun onPacketReceived(packet: ShortArray)

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun onRecordingPaused()

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun onRecordingStarted()

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun prepareRecording(
    context: Context,
    audioHandlerListener: AudioHandlerListener,
    entry: Entry
  )
}
