package com.example.uvents.ui.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.WelcomeController

class CityFragment(private val ctrl: WelcomeController) : Fragment() {

    private lateinit var typeCityButton: Button
    private lateinit var useLocationButton: Button
    private lateinit var etInputCity: EditText
    private lateinit var insertCityButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_city, container, false)

        if (v != null) {
            typeCityButton = v.findViewById(R.id.typeCityBtn)
            useLocationButton = v.findViewById(R.id.useLocationBtn)
            etInputCity = v.findViewById(R.id.etInputCity)
            insertCityButton = v.findViewById(R.id.btnInsertCity)
        }
        return v
    }
}