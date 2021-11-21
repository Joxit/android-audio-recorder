package dev.joxit.androidapp.audiorecorder.activity.dialog

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import dev.joxit.androidapp.audiorecorder.R

object DialogFactory {
  fun createTitleView(layoutInflater: LayoutInflater, title: Int): View {
    val linearLayout = layoutInflater.inflate(R.layout.dialog_title, null) as LinearLayout
    val textView: TextView = linearLayout.findViewById(R.id.title)
    textView.setText(title)
    return linearLayout
  }

  fun createListItemView(
    layoutInflater: LayoutInflater,
    view: View?,
    icon: Int,
    title: Int,
    description: Int
  ): View {
    val relativeLayout: View =
      view ?: layoutInflater.inflate(R.layout.dialog_mode_quality_item, null) as RelativeLayout
    relativeLayout.findViewById<ImageView>(R.id.icon).setImageResource(icon)
    relativeLayout.findViewById<TextView>(R.id.title).setText(title)
    relativeLayout.findViewById<TextView>(R.id.description).setText(description)
    return relativeLayout
  }
}