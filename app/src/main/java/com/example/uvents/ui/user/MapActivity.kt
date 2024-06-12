package com.example.uvents.ui.user

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.example.uvents.ui.user.menu_frgms.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Activity with the map and every events on it
 */
class MapActivity : AppCompatActivity() {

    private lateinit var mapController: MapController
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var mapFragment: MapFragment

    /**
     * On creation create a mapController and recover the user in it
     * then launch the fragment with the map, by filling it
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigation = findViewById(R.id.bottomNav)
        mapController = MapController(this)
        mapController.setUser(intent.getStringExtra("uid"))

        mapFragment = MapFragment(mapController)

        replaceFragment(mapFragment)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(mapFragment)
                    mapFragment.updateMap() //todo poco efficente
                    true
                }

                R.id.ticket -> {
                    // Handle "Booked" action
                    true
                }

                R.id.chat -> {
                    // Handle "Chat" action
                    true
                }

                R.id.profile -> {
                    mapController.setPersonalPage()
                    true
                }

                else -> false
            }
        }

        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        }
    }


    /**
     * Function that replace the fragment in the activity
     */
    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frgMapContainer, fragment)
        fragmentTransaction.commit()
    }


    private companion object {

        private const val PERMISSIONS_REQUEST_LOCATION = 0

        fun Context.isPermissionGranted(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}