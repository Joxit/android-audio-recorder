package dev.joxit.androidapp.audiorecorder.activity

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.internal.view.SupportMenu
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SectionDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import dev.joxit.androidapp.audiorecorder.BuildConfig
import dev.joxit.androidapp.audiorecorder.R
import dev.joxit.androidapp.audiorecorder.activity.dialog.mictest.MicTestDialogFragment

class RecorderDrawer(activity: AppCompatActivity) {
  private val drawer: Drawer

  companion object {
    private var identifier = -1L
    private const val FILE_STORAGE = 1L
    private const val MICROPHONE_TEST = 2L
    private const val DESCRIPTION_LEGAL = 3L
    private const val VERSION = 4L

  }

  init {
    val settingsTitle = SectionDrawerItem()
      .withName(R.string.AURE_TITLE_SETTINGS)
      .withTextColor(SupportMenu.CATEGORY_MASK)

    val informationTitle = SectionDrawerItem()
      .withName(R.string.AURE_SETTINGS_INFORMATION)
      .withTextColor(SupportMenu.CATEGORY_MASK)

    val mFileStorageItem = PrimaryDrawerItem()
      .withSelectable(false)
      .withIdentifier(FILE_STORAGE)
      .withName(R.string.AURE_SETTINGS_FILE_STORAGE)
      .withIcon(R.drawable.ic_folder_open)
      .withDescription(getFileStorageLocation(null))

    val microphoneDrawerItem = PrimaryDrawerItem()
      .withSelectable(false)
      .withIdentifier(MICROPHONE_TEST)
      .withName(R.string.AURE_DIALOG_TITLE_MICROPHONE_TEST)
      .withIcon(R.drawable.ic_keyboard_voice)

    val legalDrawerItem = PrimaryDrawerItem()
      .withSelectable(false)
      .withIdentifier(DESCRIPTION_LEGAL)
      .withName(R.string.AURE_SETTINGS_DESCRIPTION_LEGAL)
      .withIcon(R.drawable.ic_info_outline)

    val versionDrawerItem = PrimaryDrawerItem()
      .withSelectable(false)
      .withIdentifier(VERSION)
      .withName(R.string.AURE_SETTINGS_TITLE_VERSION)
      .withIcon(R.drawable.ic_format_list_numbered)
      .withDescription(BuildConfig.VERSION_NAME)

    drawer = DrawerBuilder()
      .withActivity(activity)
      .withToolbar(activity.findViewById(R.id.toolbar))
      .withHeader(R.layout.drawer_header)
      .addDrawerItems(
        settingsTitle,
        mFileStorageItem,
        microphoneDrawerItem,
        informationTitle,
        legalDrawerItem,
        versionDrawerItem
      )
      .withOnDrawerItemClickListener(this::drawerItemListener)
      .withOnDrawerListener(DrawerListener(activity.supportFragmentManager))
      .build()
  }

  private fun drawerItemListener(
    view: View,
    index: Int,
    iDrawerItem: IDrawerItem<Any, RecyclerView.ViewHolder>
  ): Boolean {
    drawer.closeDrawer()
    identifier = iDrawerItem.identifier
    return true
  }

  private fun getFileStorageLocation(a: Any?): String? = null

  private class DrawerListener(val fragmentManager: FragmentManager) : Drawer.OnDrawerListener {
    override fun onDrawerOpened(drawerView: View?) {
    }

    override fun onDrawerClosed(drawerView: View?) {
    }

    override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
      when (identifier) {
        FILE_STORAGE -> { //FileStorageDialog.show(this@MainActivity.supportFragmentManager)
        }
        MICROPHONE_TEST -> {
          MicTestDialogFragment().show(
            fragmentManager,
            MicTestDialogFragment::class.java.canonicalName
          )
        }
        DESCRIPTION_LEGAL -> { // NoticesDialogFragment.show(this@MainActivity)
        }
      }
      identifier = -1
    }

  }

}