package dev.joxit.androidapp.audiorecorder.activity.permission

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import dev.joxit.androidapp.audiorecorder.R
import dev.joxit.androidapp.audiorecorder.activity.permission.PermissionHelper.Companion.PERMISSIONS

class PermissionAdapter(private val mContext: Activity) : BaseAdapter() {
  override fun getItemId(position: Int): Long = position.toLong()

  override fun isEnabled(position: Int): Boolean = false

  override fun getCount(): Int = PERMISSIONS.size

  override fun getItem(position: Int): Pair<String, Int> = PERMISSIONS[position]

  override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
    val linearLayout =
      view ?: mContext.layoutInflater.inflate(R.layout.item_permission_list, viewGroup, false)
    val (permission, description) = getItem(position)
    linearLayout.findViewById<TextView>(R.id.title).text = permission
    linearLayout.findViewById<TextView>(R.id.description).setText(description)
    return linearLayout
  }
}
