package dev.joxit.androidapp.audiorecorder.ui.recorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.joxit.androidapp.audiorecorder.databinding.FragmentRecorderBinding

class RecorderFragment : Fragment() {

  private var _binding: FragmentRecorderBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    _binding = FragmentRecorderBinding.inflate(inflater, container, false)
    binding.root.minHeight = container?.height ?: 10000

    return binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}