package dev.joxit.androidapp.audiorecorder.utils

import android.content.Context
import androidx.fragment.app.FragmentActivity
import dev.joxit.androidapp.audiorecorder.activity.dialog.ModeQualityAdapter

fun FragmentActivity.getModePreference() =
  this.getPreferences(Context.MODE_PRIVATE)
    .getInt(ModeQualityAdapter.Mode.PREFERENCE, 0)
