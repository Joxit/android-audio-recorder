package dev.joxit.androidapp.audiorecorder.activity.service.recorder.manager

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import dev.joxit.androidapp.audiorecorder.media.AudioRecorder
import dev.joxit.androidapp.audiorecorder.model.Entry
import dev.joxit.androidapp.audiorecorder.model.resource.MicrophoneProvider
import dev.joxit.androidapp.audiorecorder.model.resource.RecorderStatus
import java.util.*
import java.util.concurrent.locks.ReentrantLock


class RecorderManager : Parcelable {
  private val mInitializationHandlers: MutableList<InitializationHandler>
  private val mInitializationLock: ReentrantLock
  private val mMicrophoneProvider: MicrophoneProvider
  private val mPackageHandlers: MutableList<AudioPacketHandler>
  private val mPackageLock: ReentrantLock
  private val mStatusHandlers: MutableList<RecorderStatusHandler>
  private val mStatusLock: ReentrantLock
  override fun describeContents(): Int {
    return 0
  }

  constructor(microphoneProvider: MicrophoneProvider) {
    mPackageLock = ReentrantLock()
    mInitializationLock = ReentrantLock()
    mStatusLock = ReentrantLock()
    mPackageHandlers = ArrayList(1)
    mInitializationHandlers = ArrayList(1)
    mStatusHandlers = ArrayList(1)
    mMicrophoneProvider = microphoneProvider
  }

  constructor(parcel: Parcel) {
    mPackageLock = ReentrantLock()
    mInitializationLock = ReentrantLock()
    mStatusLock = ReentrantLock()
    mMicrophoneProvider = MicrophoneProvider.valueOf(parcel.readString()!!)
    mInitializationHandlers = readParcelableList(parcel, InitializationHandler::class.java)
    mStatusHandlers = readParcelableList(parcel, RecorderStatusHandler::class.java)
    mPackageHandlers = readParcelableList(parcel, AudioPacketHandler::class.java)
  }

  companion object CREATOR : Creator<RecorderManager> {
    override fun createFromParcel(parcel: Parcel): RecorderManager {
      return RecorderManager(parcel)
    }

    override fun newArray(size: Int): Array<RecorderManager?> {
      return arrayOfNulls(size)
    }
  }

  override fun writeToParcel(parcel: Parcel, i: Int) {
    parcel.writeString(mMicrophoneProvider.name)
    parcel.writeParcelableArray(toParcelableArray(mInitializationHandlers), i)
    parcel.writeParcelableArray(toParcelableArray(mStatusHandlers), i)
    parcel.writeParcelableArray(toParcelableArray(mPackageHandlers), i)
  }

  private fun toParcelableArray(list: List<Parcelable>): Array<Parcelable> {
    return list.toTypedArray()
  }

  private fun <T> readParcelableList(
    parcel: Parcel,
    cls: Class<T>
  ): MutableList<T> {
    return parcel.readParcelableArray(cls.classLoader).orEmpty().map { it as T }.toMutableList()
  }

  fun addPackageHandler(audioPacketHandler: AudioPacketHandler) {
    mPackageHandlers.add(audioPacketHandler)
  }

  fun addInitializationHandler(initializationHandler: InitializationHandler) {
    mInitializationHandlers.add(initializationHandler)
  }

  fun addStatusHandler(recorderStatusHandler: RecorderStatusHandler) {
    mStatusHandlers.add(recorderStatusHandler)
  }

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun prepareRecording(
    context: Context,
    audioHandlerListener: AudioHandlerListener,
    entry: Entry
  ) {
    mPackageLock.lock()
    try {
      mPackageHandlers.forEach { it.prepareRecording(context, audioHandlerListener, entry) }
    } finally {
      mPackageLock.unlock()
    }
  }

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun startRecording() {
    mPackageLock.lock()
    try {
      mPackageHandlers.forEach(AudioPacketHandler::onRecordingStarted)
    } finally {
      mPackageLock.unlock()
    }
  }

  /* JADX INFO: finally extract failed */
  @Throws(AudioRecorder.AudioRecorderException::class)
  fun onRecordingPaused() {
    mInitializationLock.lock()
    try {
      mInitializationHandlers.forEach(InitializationHandler::onPauseRecording)
      mInitializationLock.unlock()
      mPackageLock.lock()
      try {
        mPackageHandlers.forEach(AudioPacketHandler::onRecordingPaused)
      } finally {
        mPackageLock.unlock()
      }
    } catch (th: Throwable) {
      mInitializationLock.unlock()
      throw th
    }
  }

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun onRecordingResumed() {
    mInitializationLock.lock()
    try {
      mInitializationHandlers.forEach(InitializationHandler::onPauseRecording)
    } finally {
      mInitializationLock.unlock()
    }
  }

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun onPacketReceived(packet: ShortArray) {
    mPackageLock.lock()
    try {
      mPackageHandlers.forEach { it.onPacketReceived(packet) }
    } finally {
      mPackageLock.unlock()
    }
  }

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun finishRecording() {
    mPackageLock.lock()
    try {
      mPackageHandlers.forEach(AudioPacketHandler::finishRecording)
    } finally {
      mPackageLock.unlock()
    }
  }

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun prepare(context: Context, entry: Entry) {
    mInitializationLock.lock()
    try {
      mInitializationHandlers.forEach { it.prepare(context, entry) }
    } finally {
      mInitializationLock.unlock()
    }
  }

  @Throws(AudioRecorder.AudioRecorderException::class)
  fun release() {
    mInitializationLock.lock()
    try {
      mInitializationHandlers.forEach(InitializationHandler::release)
    } finally {
      mInitializationLock.unlock()
    }
  }

  fun onStatusChanged(recorderStatus: RecorderStatus, context: Context) {
    mStatusLock.lock()
    try {
      mStatusHandlers.forEach { it.onStatusChanged(recorderStatus, context) }
    } finally {
      mStatusLock.unlock()
    }
  }

  fun onEntryChanged(entry: Entry) {
    mStatusLock.lock()
    try {
      mStatusHandlers.forEach { it.onEntryChanged(entry) }
    } finally {
      mStatusLock.unlock()
    }
  }

  fun onDestroy() {
    mInitializationLock.lock()
    try {
      mInitializationHandlers.clear()
      mInitializationLock.unlock()
      mPackageLock.lock()
      try {
        mPackageHandlers.clear()
        mPackageLock.unlock()
        mStatusLock.lock()
        try {
          mStatusHandlers.clear()
        } finally {
          mStatusLock.unlock()
        }
      } catch (th: Throwable) {
        mPackageLock.unlock()
        throw th
      }
    } catch (th2: Throwable) {
      mInitializationLock.unlock()
      throw th2
    }
  }
}
