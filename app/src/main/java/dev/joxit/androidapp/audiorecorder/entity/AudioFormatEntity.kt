package dev.joxit.androidapp.audiorecorder.entity

import dev.joxit.androidapp.audiorecorder.R

enum class AudioFormatEntity(
  val iconId: Int,
  val shortTitleId: Int,
  val titleId: Int,
  val descriptionId: Int
) {
  WAV(
    R.drawable.icon_superior,
    R.string.AURE_WAV_QUALITY_TITLE_SUPERIOR,
    R.string.AURE_DETAILS_BAR_QUALITY_SUPERIOR,
    R.string.AURE_WAV_QUALITY_SUBTITLE_SUPERIOR
  ),

  AAC128(
    R.drawable.icon_high,
    R.string.AURE_AAC128_QUALITY_TITLE_HIGH,
    R.string.AURE_DETAILS_BAR_QUALITY_HIGH,
    R.string.AURE_AAC128_QUALITY_SUBTITLE_HIGH
  ),

  AAC64(
    R.drawable.icon_medium,
    R.string.AURE_AAC64_QUALITY_TITLE_MEDIUM,
    R.string.AURE_DETAILS_BAR_QUALITY_MEDIUM,
    R.string.AURE_AAC64_QUALITY_SUBTITLE_MEDIUM,
  ),
  AAC32(
    R.drawable.icon_basic,
    R.string.AURE_AAC32_QUALITY_TITLE_BASIC,
    R.string.AURE_DETAILS_BAR_QUALITY_BASIC,
    R.string.AURE_AAC32_QUALITY_SUBTITLE_BASIC
  ),

}