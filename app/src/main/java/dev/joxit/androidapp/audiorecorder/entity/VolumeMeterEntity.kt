package dev.joxit.androidapp.audiorecorder.entity

import dev.joxit.androidapp.audiorecorder.R
import kotlin.math.floor
import kotlin.math.max

enum class VolumeMeterEntity(val stereo: Int, val mono: Int) {
  METER_0(R.drawable.bg_stereo, R.drawable.bg_mono),
  METER_1(R.drawable.bg_stereo_01, R.drawable.bg_mono_01),
  METER_2(R.drawable.bg_stereo_02, R.drawable.bg_mono_02),
  METER_3(R.drawable.bg_stereo_03, R.drawable.bg_mono_03),
  METER_4(R.drawable.bg_stereo_04, R.drawable.bg_mono_04),
  METER_5(R.drawable.bg_stereo_05, R.drawable.bg_mono_05),
  METER_6(R.drawable.bg_stereo_06, R.drawable.bg_mono_06),
  METER_7(R.drawable.bg_stereo_07, R.drawable.bg_mono_07),
  METER_8(R.drawable.bg_stereo_08, R.drawable.bg_mono_08),
  METER_9(R.drawable.bg_stereo_09, R.drawable.bg_mono_09),
  METER_10(R.drawable.bg_stereo_10, R.drawable.bg_mono_10);

  companion object {
    private val VALUES = values()
    private val SIZE = VALUES.size.toDouble()
    private const val THRESHOLD = 94.0

    fun fromDb(decibel: Double): VolumeMeterEntity {
      val idx =
        max(floor(SIZE - ((SIZE * decibel) / THRESHOLD)).toInt(), 0)
      return VALUES[idx]
    }
  }

}