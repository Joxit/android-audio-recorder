package dev.joxit.androidapp.audiorecorder.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import dev.joxit.androidapp.audiorecorder.databinding.AppBarMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: AppBarMainBinding
  private lateinit var mDrawer: RecorderDrawer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = AppBarMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.toolbar)

    mDrawer = RecorderDrawer(this)
  }
}