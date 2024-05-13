package com.example.uvents.controllers

import androidx.fragment.app.Fragment
import com.example.uvents.ui.WelcomeActivity

class WelcomeController(private val welcomeActivity: WelcomeActivity) {

    /**
     * Communicate with the welcome activity to switch fragment
     */
    fun switchFragment(f: Fragment){
        welcomeActivity.replaceFragment(f)
    }
}