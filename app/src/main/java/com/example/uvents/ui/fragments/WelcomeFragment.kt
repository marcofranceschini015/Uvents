package com.example.uvents.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.uvents.R

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private lateinit var insertCityButton: Button
    private lateinit var useLocationButton: Button
    private lateinit var linearLayoutSignButton: LinearLayout
    private lateinit var linearLayoutInsertCity: LinearLayout

    /**
     * All that is done when the view is created
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View? = inflater.inflate(R.layout.fragment_welcome, container, false)
        if (v != null) {
            insertCityButton = v.findViewById(R.id.insertCityBtn)
            useLocationButton = v.findViewById(R.id.useLocationBtn)
            linearLayoutInsertCity = v.findViewById(R.id.linearLayoutInsertCity)
            linearLayoutSignButton = v.findViewById(R.id.linearLayoutSignButton)
        }

        insertCityButton.setOnClickListener {
            linearLayoutSignButton.visibility = View.GONE
            linearLayoutInsertCity.visibility = View.VISIBLE
        }

        return v
    }

}