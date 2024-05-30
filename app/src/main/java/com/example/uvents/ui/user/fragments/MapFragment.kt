package com.example.uvents.ui.user.fragments

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.example.uvents.model.Event
import com.example.uvents.ui.user.MapActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import java.util.Locale


class MapFragment(private val mapActivity: MapActivity, private val mapController: MapController) : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var events: ArrayList<Event>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /**
     * All that is done when the view is created
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View? = inflater.inflate(R.layout.fragment_map, container, false)
        if (v != null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mapActivity)
            mapView = MapView(mapActivity)

            ///// Try some events
            events = ArrayList()
            val event1 = Event("Festa universitaria", "Unibs", "Party", "Prova", "Via Branze, 38, 25123 Brescia BS")
            val event2 = Event("Mostra di Picasso", "Belle Arti Brescia", "Mostra d'arte", "Prova prova", "Piazza della Vittoria, Brescia BS")
            val event3 = Event("Eras Tour Taylor Swift", "San Siro Concerts", "Concerto", "Prova prova prova", "Piazzale Angelo Moratti, 20151 Milano MI")
            events.add(event1)
            events.add(event2)
            events.add(event3)
        }

        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)
        mapActivity.setContentView(mapView)

        if(mapActivity.intent.getBooleanExtra("btnLocalitation", true)) {

            mapController.getCurrentLocation(fusedLocationProviderClient, mapView, events)

        } else {
            val geocoder = Geocoder(mapActivity, Locale.getDefault())
            val addresses = mapActivity.intent.getStringExtra("city")
                ?.let { geocoder.getFromLocationName(it, 1) }
            if (addresses!!.isEmpty()) {
                Toast.makeText(mapActivity, "Location inexistent or not found", Toast.LENGTH_LONG).show()
            } else {
                val address = addresses[0]
                val cityLongitude = address.longitude
                val cityLatitude = address.latitude
                mapController.getCityLocation(mapView, cityLatitude, cityLongitude, events)
            }
        }

        return v
    }

}