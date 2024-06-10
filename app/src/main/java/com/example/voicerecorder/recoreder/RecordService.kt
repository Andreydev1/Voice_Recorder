package com.example.voicerecorder.recoreder

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.IBinder
import android.provider.DocumentsContract.Root
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.voicerecorder.R
import com.example.voicerecorder.RootActivity
import com.example.voicerecorder.database.RecordDataBase
import com.example.voicerecorder.database.RecordDataBaseDao
import com.example.voicerecorder.database.RecordingItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

class RecordService : Service() {
    private var fileName: String? = null
    private var filePath: String? = null
    private var countRecords: Int? = null
    private var mediaRecorder: MediaRecorder? = null
    private var elapsedTimeInMills: Long = 0L
    private var startingTimeInMills: Long = 0L

    private var recordDataBaseDao: RecordDataBaseDao? = null
    private var recordJob = Job()
    private var recordUiScope = CoroutineScope(Dispatchers.Main + recordJob)

    private val CHANEL_ID = "recordService"

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        recordDataBaseDao = RecordDataBase.getInstance(application).recordDataBaseDao
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        countRecords = intent?.extras!!["COUNT"] as Int?

        startRecording()
        return START_STICKY
    }

    private fun startRecording() {
        setFileNameAndPath()

        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setOutputFile(filePath)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setAudioEncodingBitRate(192000)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            startingTimeInMills = System.currentTimeMillis()
            startForeground(1, createNotification())
        } catch (e: IOException) {
            Log.e("RecordService", "prepare failed")
        }
    }

    private fun createNotification(): Notification? {
        val mediaBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, CHANEL_ID)
                .setSmallIcon(R.drawable.notification_error_ic)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_error))
                .setOngoing(true)
        mediaBuilder.setContentIntent(
            PendingIntent.getActivities(applicationContext, 0, arrayOf(
                Intent(
                applicationContext, RootActivity :: class.java
            )
            ), PendingIntent.FLAG_IMMUTABLE
            )
        )
        return mediaBuilder.build()
    }

    private fun setFileNameAndPath(){
        var count = 0
        var f: File
        val dataTime = SimpleDateFormat("yyyy_MM_DD_HH_MM_SS").format(System.currentTimeMillis())

        do {
            fileName = (getString(R.string.default_file_name)
                    + "_" + dataTime + count + ".mp4")
            filePath = application.getExternalFilesDir(null)?.absolutePath
            filePath += "/$fileName"

            count++

            f = File(filePath)
        }
            while (f.exists() && !f.isDirectory)
    }

    private fun stopRecording(){
        val recordingItem = RecordingItem()

        mediaRecorder?.stop()
        elapsedTimeInMills = System.currentTimeMillis() - startingTimeInMills
        mediaRecorder?.release()
        Toast.makeText(this, getString(R.string.recording_toast), Toast.LENGTH_SHORT).show()
        recordingItem.name = fileName.toString()
        recordingItem.filePath = filePath.toString()
        recordingItem.length = elapsedTimeInMills
        recordingItem.time = System.currentTimeMillis()

        mediaRecorder = null

        try {
            recordUiScope.launch {
                withContext(Dispatchers.IO){
                    recordDataBaseDao?.insert(recordingItem)
                }
            }
        }
        catch (e:Exception){
            Log.e("RecordService", "stopRecordingException", e)
        }
    }

    override fun onDestroy() {
        if (mediaRecorder != null){
            stopRecording()
        }
        super.onDestroy()
    }
}