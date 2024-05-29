package com.example.uvents.ui.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.uvents.controllers.WelcomeController
import com.example.uvents.R
import com.google.firebase.database.ValueEventListener

class WelcomeFragment(private val ctrl: WelcomeController, private val isSigned: Boolean) : Fragment(R.layout.fragment_welcome) {

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
            btnSignIn = v.findViewById(R.id.btnSignIn)
            btnSignUp = v.findViewById(R.id.btnSignUp)
        }

        // when button sign in clicked
        // pass through the sign in fragment
        btnSignIn.setOnClickListener {
            ctrl.switchFragment(SignInFragment(ctrl))
        }

        // when button sign in clicked
        // pass through the sign up fragment
        btnSignUp.setOnClickListener {
            ctrl.switchFragment(SignUpFragment(ctrl))
        }

        return v
    }

}