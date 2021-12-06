package dev.joxit.androidapp.audiorecorder.entity

import dev.joxit.androidapp.audiorecorder.R
import kotlin.math.ceil
import kotlin.math.floor

enum class AudioFormatEntity(
  override val iconId: Int,
  override val shortTitleId: Int,
  override val titleId: Int,
  override val descriptionId: Int,
  val bitRate: Int,
  val compressionFactor: Double
) : IconTitleDescription {
  WAV(
    R.drawable.icon_superior,
    R.string.AURE_WAV_QUALITY_TITLE_SUPERIOR,
    R.string.AURE_DETAILS_BAR_QUALITY_SUPERIOR,
    R.string.AURE_WAV_QUALITY_SUBTITLE_SUPERIOR,
    705600,
    1.0,
  ),

  AAC128(
    R.drawable.icon_high,
    R.string.AURE_AAC128_QUALITY_TITLE_HIGH,
    R.string.AURE_DETAILS_BAR_QUALITY_HIGH,
    R.string.AURE_AAC128_QUALITY_SUBTITLE_HIGH,
    128000,
    1.0191,
  ),

  AAC64(
    R.drawable.icon_medium,
    R.string.AURE_AAC64_QUALITY_TITLE_MEDIUM,
    R.string.AURE_DETAILS_BAR_QUALITY_MEDIUM,
    R.string.AURE_AAC64_QUALITY_SUBTITLE_MEDIUM,
    64000,
    1.04767,
  ),
  AAC32(
    R.drawable.icon_basic,
    R.string.AURE_AAC32_QUALITY_TITLE_BASIC,
    R.string.AURE_DETAILS_BAR_QUALITY_BASIC,
    R.string.AURE_AAC32_QUALITY_SUBTITLE_BASIC,
    32000,
    1.0855,
  );

  fun getContainer() = if (this == WAV) "wav" else "aac"

  fun getFormat() = if (this == WAV) "WAV" else "AAC LC"

  fun getMimeType() = if (this == WAV) "audio/raw" else "audio/m4a-latm"

  fun getSampleRate(): Int = 44100

  fun getBitsPerSample(): Short = 16

  fun getMaxFileSize(): Long = if (this == WAV) 2146435071 else -1

  fun getBufferSize() = 0

  fun getSampleSize(duration: Double, channels: Short): Long =
    ceil(duration * ((bitRate * channels) / 8) * compressionFactor).toLong()

  fun getEstimatedTime(s: Short, j: Long): Long =
    floor((j / getSampleSize(1.0, s)).toDouble()).toLong()
}