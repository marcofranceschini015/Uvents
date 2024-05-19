package com.example.uvents

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MapActivity : AppCompatActivity() {

    private lateinit var latitude: TextView
    private lateinit var longitude: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        latitude = findViewById(R.id.tvLatitude)
        longitude = findViewById(R.id.tvLongitude)

        latitude.text = "Latitude: " + intent.getStringExtra("Latitude")
        longitude.text = "Longitude: " + intent.getStringExtra("Longitude")
    }
}