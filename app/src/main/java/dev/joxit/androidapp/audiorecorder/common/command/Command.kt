package dev.joxit.androidapp.audiorecorder.common.command

import android.content.Context

abstract class Command {
  fun cancel() {}
  fun execute(context: Context) {}
}