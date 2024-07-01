package com.example.uvents.controllers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R

class DeletedEventNotificationAdapter(private val eventsNames: ArrayList<String>): RecyclerView.Adapter<DeletedEventNotificationAdapter.DeletedEventNotificationViewHolder>(){

    class DeletedEventNotificationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var notificationTextView: TextView = itemView.findViewById(R.id.tvDeletedEventNotification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletedEventNotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.deleted_event_item, parent, false)
        return DeletedEventNotificationViewHolder(itemView)
    }

    /**
     * Returns the size of the data list
     */
    override fun getItemCount() = eventsNames.size

    override fun onBindViewHolder(holder: DeletedEventNotificationViewHolder, position: Int) {
        holder.notificationTextView.text = "Event " + eventsNames[position] + " that you booked has been deleted"
    }

}