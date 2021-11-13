package dev.joxit.androidapp.audiorecorder.entity

import dev.joxit.androidapp.audiorecorder.R

enum class AudioModeEntity(
  val iconId: Int,
  val shortTitleId: Int,
  val titleId: Int,
  val descriptionId: Int,
  val channel: Short
) {
  STEREO(
    R.drawable.icon_stereo,
    R.string.AURE_TITLE_CARD_MODE_STEREO,
    R.string.AURE_DETAILS_BAR_MODE_STEREO,
    R.string.AURE_DESCRIPTION_CARD_MODE_STEREO,
    2.toShort()
  ),
  MONO(
    R.drawable.icon_mono,
    R.string.AURE_TITLE_CARD_MODE_MONO,
    R.string.AURE_DETAILS_BAR_MODE_MONO,
    R.string.AURE_DESCRIPTION_CARD_MODE_MONO,
    1.toShort()
  );
}