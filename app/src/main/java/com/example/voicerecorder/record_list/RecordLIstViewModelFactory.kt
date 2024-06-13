package com.example.voicerecorder.record_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.voicerecorder.database.RecordDataBaseDao
import com.example.voicerecorder.record_list.ui.RecordListViewModel

class RecordLIstViewModelFactory(private val dataBaseDao: RecordDataBaseDao)
    :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordListViewModel :: class.java)){
            return RecordListViewModel(dataBaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}