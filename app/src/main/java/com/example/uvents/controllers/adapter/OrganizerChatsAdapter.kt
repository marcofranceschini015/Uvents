package com.example.uvents.controllers.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.model.Event
import com.example.uvents.ui.user.menu_frgms.ChatActivity

class OrganizerChatsAdapter(val context: Context, private val eventList: MutableList<Event>): RecyclerView.Adapter<OrganizerChatsAdapter.UserViewHolder>() {
    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.eventName)
        val orgName: TextView = itemView.findViewById(R.id.organizerName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.organizer_chat_name, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentEvent = eventList[position]
        holder.eventName.text = currentEvent.name
        holder.orgName.text = currentEvent.organizerName
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentEvent.name)
            intent.putExtra("uid", currentEvent.uid)
            intent.putExtra("eid", currentEvent.eid)
            context.startActivity(intent)
        }
    }
}