package dev.joxit.androidapp.audiorecorder.activity.dialog.mictest

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import dev.joxit.androidapp.audiorecorder.R
import dev.joxit.androidapp.audiorecorder.activity.dialog.DialogFactory
import dev.joxit.androidapp.audiorecorder.activity.permission.PermissionHelper

class MicTestDialogFragment : DialogFragment() {

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


}