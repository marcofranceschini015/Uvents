package com.example.uvents.controllers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R

class CategoryAdapter(private val categories: Array<String>): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(){

    private var checkedState = HashMap<Int, Boolean>()

    class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var catTextView: TextView = itemView.findViewById(R.id.category_text)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkboxItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(itemView)
    }

    /**
     * Returns the size of the data list
     */
    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.catTextView.text = categories[position]
        holder.checkBox.isChecked = checkedState[position] ?: false

        // Update checked state when checkbox is toggled
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkedState[position] = isChecked
        }
    }

    fun getCheckedItems(): List<String> {
        return categories.filterIndexed { index, _ -> checkedState[index] == true }
    }
}