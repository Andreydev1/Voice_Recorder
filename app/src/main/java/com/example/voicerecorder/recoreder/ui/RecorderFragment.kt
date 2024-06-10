package com.example.voicerecorder.recoreder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.voicerecorder.databinding.FragmentRecordBinding


class RecorderFragment : Fragment() {
    private lateinit var recordViewModel: RecorderViewModel
    private var _binding: FragmentRecordBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recorderViewModel =
            ViewModelProvider(this).get(RecorderViewModel::class.java)

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}