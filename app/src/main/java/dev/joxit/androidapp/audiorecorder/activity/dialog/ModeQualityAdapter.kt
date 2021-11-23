package dev.joxit.androidapp.audiorecorder.activity.dialog

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import dev.joxit.androidapp.audiorecorder.activity.MainActivity
import dev.joxit.androidapp.audiorecorder.entity.AudioFormatEntity
import dev.joxit.androidapp.audiorecorder.entity.AudioModeEntity
import dev.joxit.androidapp.audiorecorder.entity.IconTitleDescription

abstract class ModeQualityAdapter(
  private val mContext: Activity,
  private val mListView: ListView,
  private val dialog: ModeQualityDialog,
) : BaseAdapter(), AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

  private val mSelectedPosition: Int by lazy {
    mContext.getPreferences(Context.MODE_PRIVATE).getInt(getPreferencesKey(), 0)
  }

  abstract fun getPreferencesKey(): String;

  abstract fun getItems(): Array<out IconTitleDescription>

  private fun setSelected(mode: Int) {
    mContext.getPreferences(Context.MODE_PRIVATE)
      .edit()
      .putInt(getPreferencesKey(), mode)
      .apply()
    dialog.dismiss()
    if (mContext is MainActivity) {
      mContext.updateAudioModeAndFormat()
    }
  }

  override fun getCount() = getItems().size

  override fun getItem(i: Int) = getItems()[i]

  override fun getItemId(i: Int): Long = i.toLong()

  override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
    val item = getItem(i)
    val itemView = DialogFactory.createListItemView(
      mContext.layoutInflater,
      view,
      item.iconId,
      item.titleId,
      item.descriptionId
    )

    if (i == this.mSelectedPosition) {
      this.mListView.setSelection(i)
      this.mListView.setItemChecked(i, true)
    }

    return itemView
  }

  override fun onItemClick(parent: AdapterView<*>?, view: View?, positon: Int, id: Long) {
    setSelected(positon)
  }

  override fun onItemLongClick(
    parent: AdapterView<*>?,
    view: View?,
    position: Int,
    id: Long
  ): Boolean {
    setSelected(position)
    return true
  }

  class Mode(
    mContext: Activity,
    mListView: ListView,
    dialog: ModeQualityDialog,
  ) : ModeQualityAdapter(mContext, mListView, dialog) {

    override fun getPreferencesKey(): String = PREFERENCE

    override fun getItems(): Array<out IconTitleDescription> = ITEMS

    companion object {
      val ITEMS = AudioModeEntity.values()
      const val PREFERENCE = "MODE_PREFERENCES"
    }
  }

  class Quality(
    mContext: Activity,
    mListView: ListView,
    dialog: ModeQualityDialog,
  ) : ModeQualityAdapter(mContext, mListView, dialog) {

    override fun getPreferencesKey(): String = PREFERENCE

    override fun getItems(): Array<out IconTitleDescription> = ITEMS

    companion object {
      val ITEMS = AudioFormatEntity.values()
      const val PREFERENCE = "QUALITY_PREFERENCES"
    }
  }

}