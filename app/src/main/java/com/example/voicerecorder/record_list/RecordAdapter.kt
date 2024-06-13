package com.example.voicerecorder.record_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicerecorder.R
import com.example.voicerecorder.database.RecordingItem
import java.util.concurrent.TimeUnit

class RecordAdapter: RecyclerView.Adapter<RecordAdapter.ListRecordViewHolder>() {
    var data = listOf<RecordingItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    class ListRecordViewHolder private constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var vName: TextView = itemView.findViewById(R.id.file_name_text)
        var vLength: TextView = itemView.findViewById(R.id.file_length_text)
        var cardView: View = itemView.findViewById(R.id.card_view)

        companion object {
            fun from(parent: ViewGroup): ListRecordViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view: View = layoutInflater.inflate(R.layout.record_list_item, parent, false)
                return ListRecordViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRecordViewHolder {
        return ListRecordViewHolder.from(parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListRecordViewHolder, position: Int) {
        val context = holder.itemView.context
        val recordingItem = data[position]
        val itemDuration = recordingItem.length
        val minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(minutes)

        holder.vName.text = recordingItem.name
        holder.vLength.text = String.format("%02d:%02d", minutes, seconds)

    }
}