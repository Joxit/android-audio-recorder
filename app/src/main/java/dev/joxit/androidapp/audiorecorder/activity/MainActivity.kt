package dev.joxit.androidapp.audiorecorder.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dev.joxit.androidapp.audiorecorder.R
import dev.joxit.androidapp.audiorecorder.activity.dialog.ModeQualityDialog
import dev.joxit.androidapp.audiorecorder.databinding.AppBarMainBinding

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
  }

  private fun configureViewModel() {
    val modeIcon: ImageView = findViewById(R.id.mode_icon)
    val modeDescription: TextView = findViewById(R.id.mode_description)
    val qualityIcon: ImageView = findViewById(R.id.quality_icon)
    val qualityDescription: TextView = findViewById(R.id.quality_description)

    viewModel.audioMode.observe(this) {
      modeIcon.setImageResource(it.iconId)
      modeDescription.setText(it.shortTitleId)
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
}