package com.example.uvents.controllers

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.uvents.R
import com.example.uvents.model.Event
import com.example.uvents.model.EventFetcher
import com.example.uvents.ui.user.MapActivity
import com.example.uvents.ui.user.menu_frgms.EventFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import java.util.Locale


/**
 * Class that is useful to manage
 * all the annotations in a map
 * adding or removing some of that
 */
class AnnotationManager(
    private val mapView: MapView,
    private val mapActivity: MapActivity,
    private val menuController: MenuController,
    private val eventFetcher: EventFetcher) {

    // Save a connection between event an annotation on the map
    private val eventAnnotations = mutableMapOf<Event, PointAnnotation>()


    /**
     * Get the current location, set the pointer on the
     * position, and locate the camera around the position
     */
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        if (ActivityCompat.checkSelfPermission(
                mapActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                mapActivity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                mapActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        var latitude: Double?
        var longitude: Double?
        val location = fusedLocationProviderClient.lastLocation

        // get latitude and longitude getting the location
        location.addOnSuccessListener {
            if (it != null) {
                latitude = it.latitude
                longitude = it.longitude
                val cameraPosition =
                    CameraOptions.Builder().center(Point.fromLngLat(it.longitude, it.latitude))
                        .zoom(14.0).build()
                mapView.mapboxMap.setCamera(cameraPosition)

                // set personal position
                // and add all the annotation
                addAnnotationToMap(latitude, longitude)
            } else {
                // otherwise set the camera to a default value (Milan)
                val cameraPosition =
                    CameraOptions.Builder().center(Point.fromLngLat(9.188120, 45.463619)).zoom(14.0)
                        .build()
                mapView.mapboxMap.setCamera(cameraPosition)
                addAnnotationToMap(null, null)
            }
        }
    }


    /**
     * Check if a location exists, if is written correctly
     * to add an event to the map
     */
    fun locationExist(address: String): Boolean {
        val geocoder = Geocoder(mapActivity, Locale.getDefault())
        val addresses = address.let {
            geocoder.getFromLocationName(address, 1)
        }

        return addresses!!.isNotEmpty()
    }


    /**
     * Given an event add the annotation to the map
     * setup the listener and add the link between event and annotation
     * to the mutable map
     */
    fun addSingleAnnotation(e: Event) {
        val geocoder = Geocoder(mapActivity, Locale.getDefault())
        val addresses = e.address
            .let {
                geocoder.getFromLocationName(e.address!!, 1)
            }
        var longitude = 0.0
        var latitude = 0.0
        val address = addresses?.get(0) // not empty, check before add
        longitude = address!!.longitude
        latitude = address.latitude


        // set the red marker for every event at long and lat
        bitmapFromDrawableRes(
            mapActivity,
            R.drawable.red_marker
        )?.let {

            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withIconImage(it)
                .withIconAnchor(IconAnchor.TOP)
            val pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions)

            // add to a map to take track of the annotations
            // to be more efficient in publish and remove events
            eventAnnotations[e] = pointAnnotation

            val viewAnnotationManager = mapView.viewAnnotationManager
            val viewAnnotation = viewAnnotationManager.addViewAnnotation(
                resId = R.layout.item_callout_view,
                options = viewAnnotationOptions {
                    geometry(Point.fromLngLat(longitude, latitude))
                }
            )
            viewAnnotation.findViewById<TextView>(R.id.annotation).text = e.name

            // set up a click listener for every event
            pointAnnotationManager.apply {
                addClickListener(
                    OnPointAnnotationClickListener { clickedAnnotation ->
                        if (pointAnnotation == clickedAnnotation) {
                            // when click on an annotation go to event fragment
                            // that show an events
                            mapActivity.replaceFragment(
                                EventFragment(
                                    menuController,
                                    e.name!!,
                                    e.organizerName!!,
                                    e.category!!,
                                    e.date!!,
                                    e.time!!,
                                    e.description!!,
                                    e.address!!,
                                    e.uid!!,
                                    e.imageUrl!!,
                                    e.eid!!
                                )
                            )
                        } else
                            viewAnnotation.visibility = View.VISIBLE
                        false
                    }
                )
            }
        }
    }


    /**
     * Add an annotation for every events published
     */
    fun addEventAnnotation() {
        eventFetcher.eventsData.observe(mapActivity, Observer { listevent ->
            listevent.forEach { event ->
                addSingleAnnotation(event)
            }
        })
    }


    /**
     * Add annotation to the map
     * relative of a personal position
     * @param myLatitude personal position latitude
     * @param myLongitude personal position longitude
     */
    private fun addAnnotationToMap(
        myLatitude: Double?,
        myLongitude: Double?
    ) {

        // Set up the personal position annotation
        if (myLatitude != null) {
            bitmapFromDrawableRes(
                mapActivity,
                R.drawable.your_position
            )?.let {
                val annotationApi = mapView.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(myLongitude!!, myLatitude))
                    .withIconImage(it)
                pointAnnotationManager.create(pointAnnotationOptions)
            }
        }

        // for each event published get the location from the address
        // and set the annotation on the map
    }


    /**
     * Get a bitmap from a drawable resoursce
     */
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))


    /**
     * Convert of drawable resource to a bitmap
     */
    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

}