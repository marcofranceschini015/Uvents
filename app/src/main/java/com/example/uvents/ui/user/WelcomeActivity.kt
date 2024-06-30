package com.example.uvents.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.WelcomeController
import com.example.uvents.ui.user.welcome_frgms.WelcomeFragment


class WelcomeActivity : AppCompatActivity() {

    private lateinit var welcomeController: WelcomeController
    private lateinit var ivArrow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ivArrow = findViewById(R.id.ivArrow)

        // logo like home -> onclick come back to welcome fragment
        ivArrow.setOnClickListener {
            replaceFragment(WelcomeFragment(welcomeController))
        }

        // controller to access the view and be between the model and the view
        welcomeController = WelcomeController(this)
        replaceFragment(WelcomeFragment(welcomeController))

        if(intent.getBooleanExtra("logout", false)) {
            deleteCredentials()
            Toast.makeText(this, "Your credential have been deleted", Toast.LENGTH_SHORT).show()
        }

        //check if user is previously logged
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            val email = sharedPref.getString("email", "")
            val password = sharedPref.getString("password", "")
            // User automatically log-in and navigate to menu activity
            welcomeController.signIn(email!!, password!!)
        }

    }


    /**
     * Function that replace the fragment in the activity
     */
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frgContainer, fragment)
        fragmentTransaction.commit()
        hideBack()
    }


    /**
     * Function that from WelcomeActivity go to MapActivity
     */
    fun goToYourLocalizationMap(uid: String) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("uid", uid)
        startActivity(intent)
        finish()
    }

    fun saveCredentials(email: String, password: String) {
        // If login is successful, save login state
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
        if(!isLoggedIn) {
            with(sharedPref.edit()) {
                putBoolean("is_logged_in", true)
                putString("email", email)
                putString("password", password)
                apply()
            }
            Toast.makeText(this, "Your credential have been saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteCredentials() {
        // If user logout, destroy credentials
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
        if(isLoggedIn) {
            with(sharedPref.edit()) {
                putBoolean("is_logged_in", false)
                putString("email", "")
                putString("password", "")
                apply()
            }
        }
    }


    /**
     * Hide the back arrow when you are into the
     * category choice phase
     */
    fun hideBack() {
        ivArrow.visibility = View.GONE
    }

    /**
     * Show the back arrow
     */
    fun showBack() {
        ivArrow.visibility = View.VISIBLE
    }

}