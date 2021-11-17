package dev.joxit.androidapp.audiorecorder.activity.dialog.mode

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import dev.joxit.androidapp.audiorecorder.activity.dialog.DialogFactory
import dev.joxit.androidapp.audiorecorder.entity.AudioModeEntity

class ModeAdapter(
  private val mContext: Activity,
  private val mListView: ListView,
  private val mSelectedPosition: Int
) : BaseAdapter(), AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

  companion object {
    val ITEMS = AudioModeEntity.values()
  }

  override fun getCount() = ITEMS.size

  override fun getItem(i: Int) = ITEMS[i]

  override fun getItemId(i: Int): Long = i.toLong()

  override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
    val item = getItem(i)
    val itemView = view ?: DialogFactory.createListItemView(
      mContext.layoutInflater,
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

  override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    TODO("Not yet implemented")
  }

  override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long): Boolean {
    TODO("Not yet implemented")
  }
}