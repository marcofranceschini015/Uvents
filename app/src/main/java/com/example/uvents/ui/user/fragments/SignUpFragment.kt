package com.example.uvents.ui.user.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.WelcomeController
import com.example.uvents.ui.user.WelcomeActivity

class SignUpFragment(private val ctrl: WelcomeController) : Fragment() {

    private lateinit var btnSignUp: Button
    private lateinit var tvUsername: TextView
    private lateinit var etInputUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View? = inflater.inflate(R.layout.fragment_sign_up, container, false)
        if (v != null) {
            etInputUsername = v.findViewById(R.id.etInputUsername)
            tvUsername = v.findViewById(R.id.tvUsername)
            btnSignUp = v.findViewById(R.id.btnSignUp)
            etEmail = v.findViewById(R.id.etInputEmail)
            etPassword = v.findViewById(R.id.etInputPassword)
        }

        btnSignUp.setOnClickListener {
            val name = etInputUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty())
                ctrl.signUp(name, email, password, false)
            else
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }

        return v
    }

}