package dev.joxit.androidapp.audiorecorder.activity.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import dev.joxit.androidapp.audiorecorder.R

abstract class ModeQualityDialog(private val title: Int) : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(activity)
      .setCustomTitle(
        DialogFactory.createTitleView(
          requireActivity().layoutInflater,
          title
        )
      )
      .setView(buildView())
      .create()
  }

  protected abstract fun getAdapter(activity: Activity, listView: ListView): ModeQualityAdapter;

  private fun buildView(): View {
    val view = requireActivity().layoutInflater.inflate(R.layout.dialog_mode_quality_list, null)
    val list: ListView = view.findViewById(R.id.list)
    val adapter = getAdapter(requireActivity(), list)
    list.choiceMode = 1
    list.adapter = adapter
    list.onItemClickListener = adapter
    list.onItemLongClickListener = adapter
    return view
  }

  class Mode : ModeQualityDialog(R.string.AURE_SETTINGS_TITLE_AUDIO_MODE) {
    override fun getAdapter(activity: Activity, listView: ListView) =
      ModeQualityAdapter.Mode(requireActivity(), listView)
  }

  class Quality : ModeQualityDialog(R.string.AURE_SETTINGS_TITLE_AUDIO_QUALITY) {
    override fun getAdapter(activity: Activity, listView: ListView) =
      ModeQualityAdapter.Quality(requireActivity(), listView)
  }
}