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


class MapFragment(private val mapController: MapController) : Fragment() {

    //private lateinit var mapView: MapView
    //private lateinit var events: ArrayList<Event>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /**
     * All that is done when the view is created
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)

        // Get the MapView from the inflated layout
        val mapView: MapView = view.findViewById(R.id.mapView)

        // Initialize the Mapbox map within this MapView
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            // You can perform additional map setups here after the style has loaded
        }

        // Set up location services and event handling
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Dummy data for events, consider retrieving this from a ViewModel or similar component
        val events = arrayListOf(
            Event("Festa universitaria", "Unibs", "Party", "Prova", "Via Branze, 38, 25123 Brescia BS"),
            Event("Mostra di Picasso", "Belle Arti Brescia", "Mostra d'arte", "Prova prova", "Piazza della Vittoria, Brescia BS"),
            Event("Eras Tour Taylor Swift", "San Siro Concerts", "Concerto", "Prova prova prova", "Piazzale Angelo Moratti, 20151 Milano MI")
        )

        // Example of handling location logic based on intent extras
        if(requireActivity().intent.getBooleanExtra("btnLocalitation", true)) {
            // Assuming mapController is properly initialized and handles obtaining the current location
            mapController.getCurrentLocation(fusedLocationProviderClient, mapView, events)
        } else {
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val addressName = requireActivity().intent.getStringExtra("city")
            addressName?.let {
                val addresses = geocoder.getFromLocationName(it, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val cityLongitude = address.longitude
                        val cityLatitude = address.latitude
                        mapController.getCityLocation(mapView, cityLatitude, cityLongitude, events)
                    } else {
                        Toast.makeText(requireActivity(), "Location inexistent or not found", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Return the fully set-up view
        return view
    }


}