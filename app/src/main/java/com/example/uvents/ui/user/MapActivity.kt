package com.example.uvents.ui.user

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.ui.user.fragments.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MapActivity : AppCompatActivity() {

//    private lateinit var mapController: MapController
//    lateinit var mapView: MapView
////  private lateinit var mPointsList: ArrayList<OtherPoints>
//    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var bottomNavigation: BottomNavigationView

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

//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        mapController = MapController(this)

        bottomNavigation = findViewById(R.id.bottomNav)

        replaceFragment(MapFragment(this))

        bottomNavigation.visibility = View.VISIBLE

//        mapView = findViewById(R.id.mapView)

//        mapView = MapView(this)
//        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)
//        setContentView(mapView)
//
//        if(intent.getBooleanExtra("btnLocalitation", true)) {
//
//            mapController.getCurrentLocation(fusedLocationProviderClient, mapView)
//
//        } else {
//            val geocoder = Geocoder(this, Locale.getDefault())
//            val addresses = intent.getStringExtra("city")
//                ?.let { geocoder.getFromLocationName(it, 1) }
//            if (addresses!!.isEmpty()) {
//                Toast.makeText(this, "Location inexistent or not found", Toast.LENGTH_LONG).show()
//            } else {
//                val address = addresses!![0]
//                val longitude = address.longitude
//                val latitude = address.latitude
//                mapController.getCityLocation(mapView, latitude, longitude)
//            }
//        }
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

}