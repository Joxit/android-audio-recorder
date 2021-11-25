package dev.joxit.androidapp.audiorecorder.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dev.joxit.androidapp.audiorecorder.R
import dev.joxit.androidapp.audiorecorder.activity.dialog.ModeQualityAdapter
import dev.joxit.androidapp.audiorecorder.activity.dialog.ModeQualityDialog
import dev.joxit.androidapp.audiorecorder.databinding.AppBarMainBinding
import dev.joxit.androidapp.audiorecorder.entity.AudioModeEntity

class MainActivity : AppCompatActivity() {

  private lateinit var binding: AppBarMainBinding
  private lateinit var mDrawer: RecorderDrawer
  private val viewModel: RecorderViewModel by viewModels {
    RecorderViewModel.RecorderViewModelFactory(application)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = AppBarMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.toolbar)

    mDrawer = RecorderDrawer(this)

    configureViewModel()
    configureModeQualityDialog()
    updateAudioModeAndFormat()
  }

  private fun configureViewModel() {
    val modeIcon: ImageView = findViewById(R.id.mode_icon)
    val modeDescription: TextView = findViewById(R.id.mode_description)
    val qualityIcon: ImageView = findViewById(R.id.quality_icon)
    val qualityDescription: TextView = findViewById(R.id.quality_description)
    val volumeMeterMono: ImageView = findViewById(R.id.volume_meter_mono)
    val volumeMeterStereoLeft: ImageView = findViewById(R.id.volume_meter_left)
    val volumeMeterStereoRight: ImageView = findViewById(R.id.volume_meter_right)

    viewModel.audioMode.observe(this) {
      modeIcon.setImageResource(it.iconId)
      modeDescription.setText(it.shortTitleId)
      val monoVisibility = if (it == AudioModeEntity.MONO) View.VISIBLE else View.INVISIBLE
      val stereoVisibility = if (it == AudioModeEntity.STEREO) View.VISIBLE else View.INVISIBLE
      volumeMeterMono.visibility = monoVisibility
      volumeMeterStereoLeft.visibility = stereoVisibility
      volumeMeterStereoRight.visibility = stereoVisibility
    }

    viewModel.audioFormat.observe(this) {
      qualityIcon.setImageResource(it.iconId)
      qualityDescription.setText(it.shortTitleId)
    }
  }

  private fun configureModeQualityDialog() {
    val modeLayout: RelativeLayout = findViewById(R.id.mode_layout)
    val qualityLayout: RelativeLayout = findViewById(R.id.quality_layout)

    modeLayout.setOnClickListener {
      ModeQualityDialog.Mode()
        .show(this.supportFragmentManager, ModeQualityDialog.Mode::class.java.canonicalName)
    }

    qualityLayout.setOnClickListener {
      ModeQualityDialog.Quality()
        .show(this.supportFragmentManager, ModeQualityDialog.Quality::class.java.canonicalName)
    }
  }

  fun updateAudioModeAndFormat() {
    val qualityPreference =
      getPreferences(MODE_PRIVATE).getInt(ModeQualityAdapter.Quality.PREFERENCE, 0)
    viewModel.audioFormat.value = ModeQualityAdapter.Quality.ITEMS[qualityPreference]

    val modePreference = getPreferences(MODE_PRIVATE).getInt(ModeQualityAdapter.Mode.PREFERENCE, 0)
    viewModel.audioMode.value = ModeQualityAdapter.Mode.ITEMS[modePreference]
  }
}