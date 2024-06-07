package com.example.uvents.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        hideBack()
    }

    /**
     * Function that from WelcomeActivity go to MapActivity
     */
    fun goToYourLocalizationMap(uid: String){
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("btnLocalitation", true)
        intent.putExtra("uid", uid)
        startActivity(intent)
        finish()
    }

    /**
     * Hide the back arrow when you are into the
     * category choice phase
     */
    fun hideBack(){
        ivArrow.visibility = View.GONE
    }

    fun showBack() {
        ivArrow.visibility = View.VISIBLE
    }

}