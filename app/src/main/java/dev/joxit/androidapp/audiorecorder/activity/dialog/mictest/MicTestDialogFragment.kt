package dev.joxit.androidapp.audiorecorder.activity.dialog.mictest

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dev.joxit.androidapp.audiorecorder.AuReApp
import dev.joxit.androidapp.audiorecorder.R
import dev.joxit.androidapp.audiorecorder.activity.dialog.DialogFactory
import dev.joxit.androidapp.audiorecorder.activity.dialog.ModeQualityAdapter
import dev.joxit.androidapp.audiorecorder.activity.permission.PermissionHelper
import dev.joxit.androidapp.audiorecorder.entity.AudioModeEntity

class MicTestDialogFragment : DialogFragment() {
  private val viewModel: MicTestDialogViewModel by viewModels {
    MicTestDialogViewModel.MicTestDialogViewModelFactory(AuReApp.context)
  }

  override fun onCreateDialog(bundle: Bundle?): Dialog {
    PermissionHelper.INSTANCE.checkPermissionAndExecute(requireActivity())
    val view: View =
      requireActivity().layoutInflater.inflate(R.layout.dialog_fragment_mic_test, null)
    return AlertDialog.Builder(activity)
      .setCustomTitle(
        DialogFactory.createTitleView(
          requireActivity().layoutInflater,
          R.string.AURE_DIALOG_TITLE_MICROPHONE_TEST
        )
      )
      .setPositiveButton(R.string.YES_BUTTON, null)
      .setView(view)
      .create()
  }

  override fun onStart() {
    super.onStart()
    val modePreference = requireActivity()
      .getPreferences(AppCompatActivity.MODE_PRIVATE)
      .getInt(ModeQualityAdapter.Mode.PREFERENCE, 0)
    val mode = ModeQualityAdapter.Mode.ITEMS[modePreference]
    val dialog = requireDialog()
    val volumeMeterMono: ImageView = dialog.findViewById(R.id.volume_meter_mono)
    val volumeMeterStereoLeft: ImageView = dialog.findViewById(R.id.volume_meter_left)
    val volumeMeterStereoRight: ImageView = dialog.findViewById(R.id.volume_meter_right)
    val stereoVisibility = if (mode == AudioModeEntity.STEREO) View.VISIBLE else View.GONE
    val monoVisibility = if (mode == AudioModeEntity.MONO) View.VISIBLE else View.GONE
    val yes = (dialog as AlertDialog).getButton(BUTTON_POSITIVE)
    volumeMeterMono.visibility = monoVisibility
    volumeMeterStereoLeft.visibility = stereoVisibility
    volumeMeterStereoRight.visibility = stereoVisibility

    when (mode) {
      AudioModeEntity.STEREO -> {
        viewModel.stereoLeft.observe(this) {
          volumeMeterStereoLeft.setImageResource(it.stereo)
        }
        viewModel.stereoRight.observe(this) {
          volumeMeterStereoRight.setImageResource(it.stereo)
        }
      }
      AudioModeEntity.MONO -> {
        viewModel.mono.observe(this) {
          volumeMeterMono.setImageResource(it.mono)
        }
      }
    }

    dialog.setCanceledOnTouchOutside(false)
    yes.isEnabled = false
    Handler().postDelayed({
      if (dialog.isShowing) {
        yes.isEnabled = true
      }
    }, 2000)
  }

}