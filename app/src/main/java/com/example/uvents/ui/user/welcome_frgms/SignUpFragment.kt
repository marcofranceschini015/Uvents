package com.example.uvents.ui.user.welcome_frgms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.WelcomeController

class SignUpFragment(private val ctrl: WelcomeController) : Fragment() {

    private lateinit var btnSignUp: Button
    private lateinit var tvUsername: TextView
    private lateinit var etInputUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when the Fragment is at least Started.
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                ctrl.switchFragment(WelcomeFragment(ctrl))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

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
            etConfirmPassword = v.findViewById(R.id.etConfirmPassword)
        }

        ctrl.showBackArrow()

        btnSignUp.setOnClickListener {
            val name = etInputUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            if (etInputUsername.text.toString().length >= 10){
                Toast.makeText(context, "Username too long", Toast.LENGTH_SHORT).show()
            } else {
                if (email.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    if (password == confirmPassword) {
                        ctrl.signUp(name, email, password)
                    } else {
                        Toast.makeText(
                            context,
                            "Password and Confirm Password fields don't match",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }
            }

        }

        return v
    }

}