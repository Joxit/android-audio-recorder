package dev.joxit.androidapp.audiorecorder.common.command

import android.content.Context

abstract class Command {
  open fun cancel() {}
  open fun execute(context: Context) {}
}