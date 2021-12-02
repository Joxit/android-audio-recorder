package dev.joxit.androidapp.audiorecorder.activity.dialog.mictest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.joxit.androidapp.audiorecorder.entity.VolumeMeterEntity

class MicTestDialogViewModel(application: Application) : AndroidViewModel(application) {
  val stereoLeft: MutableLiveData<VolumeMeterEntity> = MutableLiveData(VolumeMeterEntity.METER_0)
  val stereoRight: MutableLiveData<VolumeMeterEntity> = MutableLiveData(VolumeMeterEntity.METER_0)
  val mono: MutableLiveData<VolumeMeterEntity> = MutableLiveData(VolumeMeterEntity.METER_0)

  class MicTestDialogViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(MicTestDialogViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return MicTestDialogViewModel(application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}