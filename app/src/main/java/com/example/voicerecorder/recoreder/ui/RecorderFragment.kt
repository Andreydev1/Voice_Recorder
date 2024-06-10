package com.example.voicerecorder.recoreder.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.voicerecorder.Manifest
import com.example.voicerecorder.R
import com.example.voicerecorder.RootActivity
import com.example.voicerecorder.database.RecordDataBase
import com.example.voicerecorder.database.RecordDataBaseDao
import com.example.voicerecorder.databinding.FragmentRecordBinding


class RecorderFragment : Fragment() {
    private lateinit var viewModel: RecorderViewModel
    private lateinit var rootActivity: RootActivity

    private var count: Int? = null
    private var dataBase: RecordDataBaseDao? = null

    companion object {
        const val MY_PERMISSION_RECORD_AUDIO = 123
    }

    private var _binding: FragmentRecordBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        dataBase = context?.let {
            RecordDataBase.getInstance(it).recordDataBaseDao
        }
        dataBase?.getCount()?.observe(viewLifecycleOwner, Observer { count = it })

        rootActivity = activity as RootActivity

        viewModel = ViewModelProvider(this).get(RecorderViewModel::class.java)

        binding.recordViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        if (rootActivity.isServiceRunning()) {
            viewModel.resetTimer()
        } else {
            binding.playButton.setImageResource(R.drawable.stop)
        }

        binding.playButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), MY_PERMISSION_RECORD_AUDIO)
            }else{
                if (rootActivity.isServiceRunning()){
                    TODO()
                }
                else{
                    TODO()
                    viewModel.startTimer()
                }
            }
        }
        return binding.root
    }
private fun onRecord(start:Boolean){

}


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}