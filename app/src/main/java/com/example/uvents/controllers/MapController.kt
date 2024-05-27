package com.example.uvents.controllers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import com.example.uvents.R
import com.example.uvents.model.Event
import com.example.uvents.ui.user.EventActivity
import com.example.uvents.ui.user.MapActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import java.util.Locale

class MapController(private val mapActivity: MapActivity) {

//    fun switchFragment(f: Fragment){
//        mapActivity.replaceFragment(f)
//    }

    public fun getCityLocation(mapView: MapView, cityLatitude: Double, cityLongitude: Double, events: ArrayList<Event>) {
        if (ActivityCompat.checkSelfPermission(mapActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(mapActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mapActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
        var cameraPosition = CameraOptions.Builder().center(Point.fromLngLat(cityLongitude, cityLatitude)).zoom(14.0).build()
        mapView.mapboxMap.setCamera(cameraPosition)

        addAnnotationToMap(mapView, null, null, events)
    }

    public fun getCurrentLocation(fusedLocationProviderClient: FusedLocationProviderClient, mapView: MapView, events: ArrayList<Event>) {
        if (ActivityCompat.checkSelfPermission(mapActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(mapActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mapActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        var latitude: Double? = null
        var longitude: Double? = null
        var location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
               latitude = it.latitude
               longitude = it.longitude
                var cameraPosition = CameraOptions.Builder().center(Point.fromLngLat(it.longitude, it.latitude)).zoom(14.0).build()
                mapView.mapboxMap.setCamera(cameraPosition)

                addAnnotationToMap(mapView, latitude, longitude, events)

            } else {
                Toast.makeText(mapActivity, "Issue about your position (maybe it's off)", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addAnnotationToMap(mapView: MapView, myLatitude: Double?, myLongitude: Double?, events: ArrayList<Event>) {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        if(myLatitude != null) {
            bitmapFromDrawableRes(
                mapActivity,
                R.drawable.your_position
            )?.let {
                val annotationApi = mapView.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
// Set options for the resulting symbol layer.
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                    .withPoint(Point.fromLngLat(myLongitude!!, myLatitude!!))
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                    .withIconImage(it)
// Add the resulting pointAnnotation to the map.
                pointAnnotationManager.create(pointAnnotationOptions)
            }
        }

        events.forEach { event ->

            val geocoder = Geocoder(mapActivity, Locale.getDefault())
            val addresses = event.address
                ?.let { geocoder.getFromLocationName(it, 1) }
            var longitude: Double = 0.0
            var latitude: Double = 0.0
            if (addresses!!.isEmpty()) {
                Toast.makeText(mapActivity, "Location inexistent or not found", Toast.LENGTH_LONG).show()
            } else {
                val address = addresses!![0]
                longitude = address.longitude
                latitude = address.latitude
            }

            bitmapFromDrawableRes(
                mapActivity,
                R.drawable.red_marker
            )?.let {

                val annotationApi = mapView.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager()

                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(longitude, latitude))
                    .withIconImage(it)
                    .withIconAnchor(IconAnchor.BOTTOM)
                val pointAnnotation = pointAnnotationManager?.create(pointAnnotationOptions)

                val viewAnnotationManager = mapView.viewAnnotationManager

                val viewAnnotation = viewAnnotationManager.addViewAnnotation(
                    resId = R.layout.item_callout_view,
                    options = viewAnnotationOptions {
                        geometry(Point.fromLngLat(longitude, latitude))
                    }
                )
//            viewAnnotation.visibility = View.GONE

                viewAnnotation.findViewById<TextView>(R.id.annotation).setText("${event.name}")

//            viewAnnotation.findViewById<TextView>(R.id.annotation).setOnClickListener {
//                viewAnnotation.visibility = View.GONE
//            }

                pointAnnotationManager?.apply {
                    addClickListener(
                        OnPointAnnotationClickListener { clickedAnnotation ->
                            if (pointAnnotation == clickedAnnotation) {
                                val intent = Intent(mapActivity, EventActivity::class.java)
//                            intent.putExtra("btnLocalitation", true)
                                mapActivity.startActivity(intent)
                                mapActivity.finish()
//                                mapActivity.replaceFragment(EventFragment())
                            } else
                                viewAnnotation.visibility = View.VISIBLE
                            false
                        }
                    )
                }
            }
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
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