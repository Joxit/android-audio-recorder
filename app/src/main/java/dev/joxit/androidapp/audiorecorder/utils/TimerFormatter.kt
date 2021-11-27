package dev.joxit.androidapp.audiorecorder.utils

import java.util.*
import java.util.concurrent.TimeUnit

object TimerFormatter {
  private const val FORMAT_MASK = "%02d:%02d:%02d"

  fun formatInterval(j: Long): String? {
    val hours = TimeUnit.MILLISECONDS.toHours(j)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(j - TimeUnit.HOURS.toMillis(hours))
    val seconds = TimeUnit.MILLISECONDS.toSeconds(
      j - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes)
    )
    return String.format(
      Locale.getDefault(),
      FORMAT_MASK,
      java.lang.Long.valueOf(hours),
      java.lang.Long.valueOf(minutes),
      java.lang.Long.valueOf(seconds)
    )
  }
}