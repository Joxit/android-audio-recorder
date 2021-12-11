package dev.joxit.androidapp.audiorecorder.common.command

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.ContextCompat


abstract class IntentCommand : Command(), Parcelable {
  companion object {
    private const val INTENT_EXTRA_COMMAND = "command"
    fun getCommand(intent: Intent): IntentCommand? {
      return intent.getParcelableExtra(INTENT_EXTRA_COMMAND)
    }

    fun executeFromIntent(context: Context, intent: Intent?) {
      if (intent != null) {
        getCommand(intent)?.execute(context)
      }
    }
  }

  abstract fun createIntent(context: Context?): Intent
  override fun describeContents(): Int = 0

  fun start(context: Context?) {
    val createIntent = createIntent(context)
    createIntent.putExtra(INTENT_EXTRA_COMMAND, this)
    ContextCompat.startForegroundService(context!!, createIntent)
  }

  fun getIntent(context: Context?): Intent {
    val createIntent = createIntent(context)
    createIntent.putExtra(INTENT_EXTRA_COMMAND, this)
    return createIntent
  }
}
