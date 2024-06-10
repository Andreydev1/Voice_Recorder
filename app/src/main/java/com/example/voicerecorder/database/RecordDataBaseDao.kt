package com.example.voicerecorder.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDataBaseDao {
    @Insert
    fun insert(record: RecordingItem)

    @Update
    fun update(record: RecordingItem)

    @Query("SELECT * from recording_table WHERE id = :key")
    fun getRecord(key:Long?):RecordingItem?

    @Query("DELETE FROM recording_table")
    fun clearAll()

    @Query("DELETE FROM recording_table WHERE id=:key")
    fun deleteRecord(key: Long?)

    @Query("SELECT * FROM recording_table ORDER BY id DESC")
    fun getAllRecords():LiveData<MutableList<RecordingItem>>

    @Query("SELECT COUNT(*) FROM recording_table ")
    fun getCount(): LiveData<Int>
}