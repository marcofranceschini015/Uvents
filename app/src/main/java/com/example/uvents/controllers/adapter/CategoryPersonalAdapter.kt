package com.example.uvents.controllers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R

class CategoryPersonalAdapter(
    private var categories: MutableList<String>,
    private val onItemRemove: (Int) -> Unit
): RecyclerView.Adapter<CategoryPersonalAdapter.CategoryPersonalViewHolder>(){

    class CategoryPersonalViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var catTextView: TextView = itemView.findViewById(R.id.category_text)
        val removeButton: ImageView = itemView.findViewById(R.id.minusImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryPersonalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cateogry_profile_item, parent, false)
        return CategoryPersonalViewHolder(itemView)
    }

    /**
     * Returns the size of the data list
     */
    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: CategoryPersonalViewHolder, position: Int) {
        holder.catTextView.text = categories[position]
        holder.removeButton.setOnClickListener {
            onItemRemove(position)
        }
    }

    fun removeItem(position: Int) {
        categories.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, categories.size)
    }

}