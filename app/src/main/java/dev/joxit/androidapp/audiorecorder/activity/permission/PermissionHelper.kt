package dev.joxit.androidapp.audiorecorder.activity.permission

import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dev.joxit.androidapp.audiorecorder.R

enum class PermissionHelper {
  INSTANCE;

  companion object {
    val PERMISSIONS =
      listOf(
        "android.permission.WRITE_EXTERNAL_STORAGE" to R.string.AURE_PERMISSION_LIST_STORAGE_DESCRIPTION,
        "android.permission.READ_EXTERNAL_STORAGE" to R.string.AURE_PERMISSION_LIST_STORAGE_DESCRIPTION,
        "android.permission.READ_PHONE_STATE" to R.string.AURE_PERMISSION_LIST_PHONE_DESCRIPTION,
        "android.permission.RECORD_AUDIO" to R.string.AURE_PERMISSION_LIST_MICROPHONE_DESCRIPTION
      )
  }

  fun checkPermissionAndExecute(fragmentActivity: FragmentActivity) {
    if (!checkPermissions(fragmentActivity)) {
      RequestPermissionDialog().show(
        fragmentActivity.supportFragmentManager,
        RequestPermissionDialog::class.java.canonicalName
      )
    }
  }

  fun checkPermissions(fragmentActivity: FragmentActivity): Boolean {
    return PERMISSIONS.none { (permission, _) ->
      ContextCompat.checkSelfPermission(fragmentActivity, permission) != PERMISSION_GRANTED
    }
  }
}