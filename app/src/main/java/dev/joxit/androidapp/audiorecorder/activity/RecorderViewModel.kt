package dev.joxit.androidapp.audiorecorder.activity

import android.app.Application
import androidx.lifecycle.*
import dev.joxit.androidapp.audiorecorder.entity.AudioFormatEntity
import dev.joxit.androidapp.audiorecorder.entity.AudioModeEntity

class RecorderViewModel(application: Application) : AndroidViewModel(application) {
  val audioMode: MutableLiveData<AudioModeEntity> = MutableLiveData(AudioModeEntity.STEREO)
  val audioFormat: MutableLiveData<AudioFormatEntity> = MutableLiveData(AudioFormatEntity.WAV)


  class RecorderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(RecorderViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return RecorderViewModel(application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}