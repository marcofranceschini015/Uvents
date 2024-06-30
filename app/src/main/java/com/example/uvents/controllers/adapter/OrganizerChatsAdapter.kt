package com.example.uvents.controllers.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.model.User
import com.example.uvents.ui.user.menu_frgms.ChatActivity

class OrganizerChatsAdapter(val context: Context, private val userList: MutableList<User>,
                            private val newsList: MutableList<Int>): RecyclerView.Adapter<OrganizerChatsAdapter.UserViewHolder>() {
    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.organizerName)
        val newMessages: TextView = itemView.findViewById(R.id.notification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.organizer_chat_name, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        val currentNewMessages = newsList[position]
        holder.username.text = currentUser.name
        if(currentNewMessages == 0) {
            holder.newMessages.visibility = View.GONE
        } else {
            holder.newMessages.text = currentNewMessages.toString()
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)
            context.startActivity(intent)
            holder.newMessages.visibility = View.GONE
        }
    }
}