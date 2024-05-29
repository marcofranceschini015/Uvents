package com.example.uvents.model

import android.content.Context
import com.example.uvents.R

class CategorySource (val context: Context) {
    fun getCategoryList(): Array<String> {
        return context.resources.getStringArray(R.array.category_array)
    }
}