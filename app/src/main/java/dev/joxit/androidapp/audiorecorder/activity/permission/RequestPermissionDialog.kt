package dev.joxit.androidapp.audiorecorder.activity.permission

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PermissionInfo
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import dev.joxit.androidapp.audiorecorder.R
import dev.joxit.androidapp.audiorecorder.activity.dialog.DialogFactory
import dev.joxit.androidapp.audiorecorder.activity.permission.PermissionHelper.Companion.PERMISSIONS
import java.text.MessageFormat

class RequestPermissionDialog : DialogFragment(), DialogInterface.OnClickListener {

  override fun onCreateDialog(bundle: Bundle?): Dialog {
    return AlertDialog.Builder(requireActivity())
      .setCustomTitle(
        DialogFactory.createTitleView(
          layoutInflater,
          R.string.AURE_DIALOG_TITLE_ERROR_PERMISSION
        )
      )
      .setView(R.layout.dialog_request_permission)
      .setPositiveButton(R.string.YES_BUTTON, this)
      .create()
  }

  override fun onClick(dialog: DialogInterface?, which: Int) {
    if (which == DialogInterface.BUTTON_POSITIVE) {
      ActivityCompat.requestPermissions(
        requireActivity(),
        PERMISSIONS.map { it.first }.toTypedArray(),
        PermissionInfo.PROTECTION_FLAG_VERIFIER
      )
    }
  }

  override fun onStart() {
    super.onStart()
    val dialog = requireDialog()
    dialog.findViewById<TextView>(R.id.permission_body).text =
      MessageFormat.format(
        getString(R.string.AURE_DIALOG_BODY_ASK_PERMISSION),
        getString(R.string.app_name)
      )
    dialog.findViewById<ListView>(R.id.permission_list).adapter =
      PermissionAdapter(requireActivity())
    dialog.setCanceledOnTouchOutside(false)

  }
}