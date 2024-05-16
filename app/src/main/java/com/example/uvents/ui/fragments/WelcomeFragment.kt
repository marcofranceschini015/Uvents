package com.example.uvents.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.uvents.controllers.WelcomeController
import com.example.uvents.R

class WelcomeFragment(private val ctrl: WelcomeController) : Fragment(R.layout.fragment_welcome) {

    private lateinit var typeCityButton: Button
    private lateinit var useLocationButton: Button
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
            typeCityButton = v.findViewById(R.id.typeCityBtn)
            useLocationButton = v.findViewById(R.id.useLocationBtn)
            btnSignIn = v.findViewById(R.id.btnSignIn)
            btnSignUp = v.findViewById(R.id.btnSignUp)
        }

        typeCityButton.setOnClickListener {
            ctrl.switchFragment(CityFragment(ctrl))
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
            ctrl.switchFragment(SignUpFragment(ctrl))
        }

        return v
    }

}