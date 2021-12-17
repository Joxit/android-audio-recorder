package dev.joxit.androidapp.audiorecorder.model

import dev.joxit.androidapp.audiorecorder.entity.AudioFormatEntity

data class Entry(
  val channels: Short,
  val duration: Long,
  val format: AudioFormatEntity,
  val id: Long,
  val name: String,
  val providerId: String,
  val timestamp: Long,
  val uri: String,
)