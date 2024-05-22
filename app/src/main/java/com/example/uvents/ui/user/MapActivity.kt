package com.example.uvents.ui.user

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import java.util.Locale


class MapActivity : AppCompatActivity() {

    private lateinit var mapController: MapController
    lateinit var mapView: MapView
//    private var latitude: Double = 0.0
//    private var longitude: Double = 0.0
//  private lateinit var mPointsList: ArrayList<OtherPoints>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

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
            //getCurrentLocation()
            mapController.getCurrentLocation(fusedLocationProviderClient, mapView)
        } else {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = intent.getStringExtra("city")
                ?.let { geocoder.getFromLocationName(it, 1) }
            val address = addresses!![0]
            var longitude = address.longitude
            var latitude = address.latitude
            //getCityLocation()
            mapController.getCityLocation(mapView, latitude, longitude)
        }
    }

}