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
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.WelcomeController

class SignUpFragment(private val ctrl: WelcomeController) : Fragment() {

    private lateinit var btnSignUp: Button
    private lateinit var tvUsername: TextView
    private lateinit var etInputUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchOrganizer: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_sign_up, container, false)
        if (v != null) {
            etInputUsername = v.findViewById(R.id.etInputUsername)
            tvUsername = v.findViewById(R.id.tvUsername)
            switchOrganizer = v.findViewById(R.id.switchOrganizer)
            btnSignUp = v.findViewById(R.id.btnSignUp)
            etEmail = v.findViewById(R.id.etInputEmail)
            etPassword = v.findViewById(R.id.etInputPassword)
        }

        btnSignUp.setOnClickListener {
            val name = etInputUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            ctrl.signUp(name, email, password, switchOrganizer.isChecked)
        }

        switchOrganizer.setOnClickListener {
            if (switchOrganizer.isChecked){
               tvUsername.text = "Company name"
               etInputUsername.hint = "Company name"
            } else {
                tvUsername.text = "Username"
                etInputUsername.hint = "Username"
            }
        }


        return v
    }

}