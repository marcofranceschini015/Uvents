package com.example.uvents.ui.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.example.uvents.model.Event
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.maps.MapView
import com.mapbox.maps.Style


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
            Event("Festa universitaria", "Unibs", "Party", "Festa di Primavera per staccare dallo stress dello studio e degli esami", "Via Branze, 38, Brescia"),
            Event("Mostra di Picasso", "Belle Arti Brescia", "Mostra d'arte", "Vengono esposti i quadri più particolari dell'autore", "Piazza della Vittoria, Brescia"),
            Event("Eras Tour Taylor Swift", "San Siro Concerts", "Concerto", "Concerto di 3 ore con 40 canzoni dell'artista. Occasione unica per vedere Taylor Swift in Italia.", "Stadio San Siro, Milano")
        )

        mapController.getCurrentLocation(fusedLocationProviderClient, mapView, events)

        // Return the fully set-up view
        return view
    }


}