package com.example.uvents.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.uvents.controllers.WelcomeController
import com.example.uvents.R

class WelcomeFragment(private val ctrl: WelcomeController) : Fragment(R.layout.fragment_welcome) {

    private lateinit var insertCityButton: Button
    private lateinit var useLocationButton: Button
    private lateinit var linearLayoutSignButton: LinearLayout
    private lateinit var linearLayoutInsertCity: LinearLayout
    private lateinit var btnSignIn: Button
    private lateinit var btnSignUp: Button


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
            btnSignIn = v.findViewById(R.id.btnSignIn)
            btnSignUp = v.findViewById(R.id.btnSignUp)
        }

        insertCityButton.setOnClickListener {
            linearLayoutSignButton.visibility = View.GONE
            linearLayoutInsertCity.visibility = View.VISIBLE
        }

        useLocationButton.setOnClickListener {
            //todo
        }

        // when button sign in clicked
        // pass through the sign in fragment
        btnSignIn.setOnClickListener {
            ctrl.switchFragment(SignInFragment(ctrl))
        }

        btnSignUp.setOnClickListener {
            ctrl.switchFragment(Fragment()) //todo
        }

        return v
    }

}