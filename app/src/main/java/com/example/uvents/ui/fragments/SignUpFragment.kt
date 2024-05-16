package com.example.uvents.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.WelcomeController

class SignUpFragment(private val ctrl: WelcomeController) : Fragment() {

    private lateinit var btnSignIn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_sign_up, container, false)
        if (v != null) {
            btnSignIn = v.findViewById(R.id.btnSignIn)
        }

        btnSignIn.setOnClickListener {
            ctrl.switchFragment(SignInFragment(ctrl))
        }
        return v
    }

}