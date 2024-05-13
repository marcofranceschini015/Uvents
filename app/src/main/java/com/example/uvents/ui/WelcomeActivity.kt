package com.example.uvents.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uvents.R

class WelcomeActivity : AppCompatActivity() {

    // link the view
    private lateinit var mainLayout: LinearLayout
    private lateinit var signBtnLayout: RelativeLayout
    private lateinit var insertCityLayout: RelativeLayout
    private lateinit var insertCityBtn: Button
    private lateinit var useLocationBtn: Button
    private lateinit var etInputCity: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainLayout = findViewById(R.id.mainLayout)
        signBtnLayout = findViewById(R.id.signBtnLayout)
        insertCityBtn = findViewById(R.id.insertCityBtn)
        insertCityLayout = findViewById(R.id.insertCityLayout)
        useLocationBtn = findViewById(R.id.useLocationBtn)
        etInputCity = findViewById(R.id.etInputCity)

        // switch between layout
        insertCityBtn.setOnClickListener {
            signBtnLayout.visibility = View.GONE
            insertCityLayout.visibility = View.VISIBLE
        }

    }
}