package com.example.uvents.ui.user

import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import java.util.Locale


class MapActivity : AppCompatActivity() {

    private lateinit var mapController: MapController
    lateinit var mapView: MapView
//  private lateinit var mPointsList: ArrayList<OtherPoints>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapController = MapController(this)

        mapView = MapView(this)
        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)
        setContentView(mapView)

        if(intent.getBooleanExtra("btnLocalitation", true)) {

            mapController.getCurrentLocation(fusedLocationProviderClient, mapView)

        } else {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = intent.getStringExtra("city")
                ?.let { geocoder.getFromLocationName(it, 1) }
            if (addresses!!.isEmpty()) {
                Toast.makeText(this, "Location inexistent or not found", Toast.LENGTH_LONG).show()
            } else {
                val address = addresses!![0]
                val longitude = address.longitude
                val latitude = address.latitude
                mapController.getCityLocation(mapView, latitude, longitude)
            }
        }
    }

}