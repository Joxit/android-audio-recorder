package dev.joxit.androidapp.audiorecorder.entity

import dev.joxit.androidapp.audiorecorder.R

enum class AudioModeEntity(
  override val iconId: Int,
  override val shortTitleId: Int,
  override val titleId: Int,
  override val descriptionId: Int,
  val channel: Short
): IconTitleDescription {
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