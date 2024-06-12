package com.example.uvents.model

import android.content.Context
import com.example.uvents.R

/**
 * Class that helps to return the array
 * of category stored, that represents
 * all the categories avaiable
 */
class CategorySource (val context: Context) {

    /**
     * Return the array of strings, in which
     * every string represents a category
     */
    fun getCategoryList(): Array<String> {
        return context.resources.getStringArray(R.array.category_array)
    }
}