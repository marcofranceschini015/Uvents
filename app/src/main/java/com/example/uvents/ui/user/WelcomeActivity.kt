package com.example.uvents.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.uvents.controllers.WelcomeController
import com.example.uvents.R
import com.example.uvents.ui.user.fragments.WelcomeFragment

class WelcomeActivity : AppCompatActivity() {

    private lateinit var welcomeController: WelcomeController
    private lateinit var ivArrow: ImageView
    private lateinit var ivPerson: ImageView
    private lateinit var tvShowUsername: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ivPerson = findViewById(R.id.ivPerson)
        tvShowUsername = findViewById(R.id.tvShowUsername)
        ivPerson.visibility = View.GONE
        tvShowUsername.visibility = View.GONE

        ivArrow = findViewById(R.id.ivArrow)

        // logo like home -> onclick come back to welcome fragment
        ivArrow.setOnClickListener {
            replaceFragment(WelcomeFragment(welcomeController, false))
        }

        // controller to access the view and be between the model and the view
        welcomeController = WelcomeController(this)

        // put welcome fragment as first fragment
        // no possibility to come back
        replaceFragment(WelcomeFragment(welcomeController, false))
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

    /**
     * Function that from WelcomeActivity go to MapActivity
     */
    fun goToMap(lat: String, long: String){
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("Latitude", lat)
        intent.putExtra("Longitude", long)
        startActivity(intent)
        finish()
    }

    fun showUsername(username: String){
        ivPerson.visibility = View.VISIBLE
        tvShowUsername.visibility = View.VISIBLE
        tvShowUsername.text = username

        // change back home with sign user
        ivArrow.setOnClickListener {
            replaceFragment(WelcomeFragment(welcomeController, true))
        }
    }
}