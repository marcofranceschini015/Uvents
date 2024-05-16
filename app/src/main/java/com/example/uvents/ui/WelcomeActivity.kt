package com.example.uvents.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.uvents.controllers.WelcomeController
import com.example.uvents.R
import com.example.uvents.ui.fragments.WelcomeFragment

class WelcomeActivity : AppCompatActivity() {

    private lateinit var welcomeController: WelcomeController
    private lateinit var logo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        logo = findViewById(R.id.logo)

        // logo like home -> onclick come back to welcome fragment
        logo.setOnClickListener {
            replaceFragment(WelcomeFragment(welcomeController))
        }

        // controller to access the view and be between the model and the view
        welcomeController = WelcomeController(this)

        // put welcome fragment as first fragment
        // no possibility to come back
        replaceFragment(WelcomeFragment(welcomeController))
    }


    /**
     * Function that replace the fragment in the activity
     */
    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frgContainer, fragment)
        fragmentTransaction.commit()
    }
}