package dev.joxit.androidapp.audiorecorder.activity.dialog.mode

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import dev.joxit.androidapp.audiorecorder.R
import dev.joxit.androidapp.audiorecorder.activity.dialog.DialogFactory

class ModeDialog: DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(activity)
      .setCustomTitle(
        DialogFactory.createTitleView(
          requireActivity().layoutInflater,
          R.string.AURE_SETTINGS_TITLE_AUDIO_MODE
        )
      )
      .setView(buildView())
      .create()
  }

  private fun buildView() : View {
    val view = requireActivity().layoutInflater.inflate(R.layout.dialog_mode_quality_list, null)
    val list: ListView = view.findViewById(R.id.list)
    val adapter = ModeAdapter(requireActivity(), list, 0)
    list.choiceMode = 1
    list.adapter = adapter
    list.onItemClickListener = adapter
    list.onItemLongClickListener = adapter
    return view
  }
}