package com.example.voicerecorder.record_list.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.voicerecorder.database.RecordDataBaseDao
import javax.sql.CommonDataSource

class RecordListViewModel(dataSource: RecordDataBaseDao) : ViewModel() {

val database = dataSource
    val records = database.getAllRecords()
}