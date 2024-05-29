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


class SignInFragment(private val ctrl: WelcomeController) : Fragment(R.layout.fragment_sign_in) {

    private lateinit var btnSignIn: Button
    private lateinit var etInputEmailSi: EditText
    private lateinit var etInputPasswordSi: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View? = inflater.inflate(R.layout.fragment_sign_in, container, false)

        if (v != null) {
            etInputEmailSi = v.findViewById(R.id.etInputEmailSi)
            etInputPasswordSi = v.findViewById(R.id.etInputPasswordSi)
            btnSignIn = v.findViewById(R.id.btnSignIn)
        }

        btnSignIn.setOnClickListener {
            val email = etInputEmailSi.text.toString()
            val password = etInputPasswordSi.text.toString()
            ctrl.signIn(email, password)
        }

        return v
    }

}