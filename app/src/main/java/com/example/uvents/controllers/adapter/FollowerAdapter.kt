package com.example.uvents.controllers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R

class FollowerAdapter (private val followersMap: MutableMap<String, String>,
                       private val onItemRemoved: (String) -> Unit
) : RecyclerView.Adapter<FollowerAdapter.ViewHolder>() {

    private var keys = followersMap.keys.toList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFollowerName: TextView = view.findViewById(R.id.category_text)
        val btnDelete: ImageView = view.findViewById(R.id.minusImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = keys[position]
        holder.tvFollowerName.text = followersMap[key]
        holder.btnDelete.setOnClickListener {
            onItemRemoved(key)
        }
    }

    override fun getItemCount(): Int = keys.size

    fun removeItem(key: String) {
        val position = keys.indexOf(key)
        if (position != -1) {
            followersMap.remove(key)
            keys = followersMap.keys.toList()  // Update keys after removal
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }
}