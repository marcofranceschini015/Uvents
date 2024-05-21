package com.example.uvents.ui.organizer

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uvents.R

class DashboardOrganizerActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etCompanyName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard_organizer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etEmail = findViewById(R.id.etEmail)
        etCompanyName = findViewById(R.id.etCompanyName)

        etEmail.setText(intent.getStringExtra("EMAIL"))
        etCompanyName.setText(intent.getStringExtra("NAME"))
    }
}