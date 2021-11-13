package dev.joxit.androidapp.audiorecorder.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import dev.joxit.androidapp.audiorecorder.R
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
  }

  private fun configureViewModel() {
    val modeIcon: ImageView = findViewById(R.id.mode_icon)
    val modeDescription: TextView = findViewById(R.id.mode_description)

    viewModel.audioMode.observe(this) {
      modeIcon.setImageResource(it.iconId)
      modeDescription.setText(it.shortTitleId)
    }

  }
}