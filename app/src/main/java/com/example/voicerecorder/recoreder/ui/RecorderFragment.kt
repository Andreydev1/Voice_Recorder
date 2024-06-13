package com.example.voicerecorder.recoreder.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.voicerecorder.R
import com.example.voicerecorder.RootActivity
import com.example.voicerecorder.database.RecordDataBase
import com.example.voicerecorder.database.RecordDataBaseDao
import com.example.voicerecorder.databinding.FragmentRecordBinding

import com.example.voicerecorder.recoreder.RecordService
import java.io.File


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
            binding.playButton.setImageResource(R.drawable.ic_stop_button)
        }

        binding.playButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSION_RECORD_AUDIO
                )
            } else {
                if (rootActivity.isServiceRunning()) {
                    onRecord(false)
                    viewModel.stopTimer()
                } else {
                    viewModel.startTimer()
                }
            }
        }
        createChannel(getString(R.string.notification_channel_id),
                        getString(R.string.notification_channel_name))

        return binding.root
    }

    private fun onRecord(start: Boolean) {
        val intent = Intent(activity, RecordService::class.java)
        intent.putExtra("COUNT", count)

        if (start) {
            binding.playButton.setImageResource(R.drawable.ic_stop_button)
            Toast.makeText(activity, R.string.recording_toast, Toast.LENGTH_SHORT).show()

            val folder =
                File(activity?.getExternalFilesDir(null)?.absolutePath.toString() + "VoiceRecorder")
            if (!folder.exists()) {
                folder.mkdir()
            }
            activity?.startService(intent)
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            binding.playButton.setImageResource(R.drawable.ic_mic)

            activity?.stopService(intent)
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED)
                   onRecord(true)
                viewModel.startTimer()
            }else ->{
                Toast.makeText(activity, getString(R.string.recording_toast_permisson), Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT)
                    .apply { setShowBadge(false)
                    setSound(null, null)}
            val notificationManager = requireActivity().getSystemService(
                NotificationManager :: class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}