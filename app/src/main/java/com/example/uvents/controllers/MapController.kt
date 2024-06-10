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
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.model.Event
import com.example.uvents.model.User
import com.example.uvents.ui.user.MapActivity
import com.example.uvents.ui.user.menu_frgms.EventFragment
import com.example.uvents.ui.user.menu_frgms.PersonalPageFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
import kotlin.random.Random

class MapController(var mapActivity: MapActivity) {

    private lateinit var user: User
    private val dbUrl: String = "https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/"
    private lateinit var mDbRef: DatabaseReference

    fun switchFragment(f: Fragment){
        mapActivity.replaceFragment(f)
    }

    fun getCurrentLocation(fusedLocationProviderClient: FusedLocationProviderClient, mapView: MapView, events: ArrayList<Event>) {
        if (ActivityCompat.checkSelfPermission(mapActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(mapActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mapActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        var latitude: Double? = null
        var longitude: Double? = null
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
               latitude = it.latitude
               longitude = it.longitude
                val cameraPosition = CameraOptions.Builder().center(Point.fromLngLat(it.longitude, it.latitude)).zoom(14.0).build()
                mapView.mapboxMap.setCamera(cameraPosition)

                addAnnotationToMap(mapView, latitude, longitude, events)

            } else {
                val cameraPosition = CameraOptions.Builder().center(Point.fromLngLat(9.188120, 45.463619)).zoom(14.0).build()
                mapView.mapboxMap.setCamera(cameraPosition)

                addAnnotationToMap(mapView, null, null, events)
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
                    .withPoint(Point.fromLngLat(myLongitude!!, myLatitude))
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
                val address = addresses[0]
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
                    .withIconAnchor(IconAnchor.TOP)
                val pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions)

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

                pointAnnotationManager.apply {
                    addClickListener(
                        OnPointAnnotationClickListener { clickedAnnotation ->
                            if (pointAnnotation == clickedAnnotation) {
//                                mapActivity.hideSearchBar()
                                mapActivity.replaceFragment(EventFragment(MapController(mapActivity), event))
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


    /**
     * Recover in the database the user from its uid
     */
    fun setUser(uid: String?) {
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        mDbRef.child("user").child(uid!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // recover the User by uid passed
                user = dataSnapshot.getValue(User::class.java)!!
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    /**
     * Set up the personal page with all information in the view
     */
    fun setPersonalPage() {
        mapActivity.replaceFragment(PersonalPageFragment(this, user.name, user.email, user.categories, user.getEventNamePublished(), user.getFollowed()))
    }


    /**
     * Update a user when modifying the personal page
     */
    fun updateUser (categories: List<String>, events: List<String>, followed: List<String>) {
        // todo organizer not followed -> modify view
        user.categories = categories

        removeEvent(events) // todo send notification
        updateDatabase(categories, user.eventsPublished, user.getFollowed())
    }


    /**
     * From a list of events' name remove the event
     * in the User db and in the event db
     */
    private fun removeEvent(events: List<String>){
        val listEidToDelete = mutableListOf<String?>()
        user.eventsPublished.forEach{ event ->
            if (!events.contains(event.name)) {
                listEidToDelete.add(event.eid)
            }
        }
        user.eventsPublished = user.eventsPublished.filter { it.eid in listEidToDelete }

        // remove into db
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        val eventsRef = mDbRef.child("event")
        listEidToDelete.forEach{ eid ->
            eventsRef.child(eid.toString()).removeValue().addOnCompleteListener{}
        }
    }


    /**
     * Update the database with the passed information
     */
    private fun updateDatabase(categories: List<String>, events: List<Event>, followed: List<String>) {
        // Reference to the specific user's node
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        val userRef = mDbRef.child("user").child(user.uid)


        // Map of data to update
        val updates = hashMapOf<String, Any>(
            "categories" to categories,
            "eidPublished" to events,
            "followed" to followed
        )

        // Update children of the user node
        userRef.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(mapActivity, "Updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(mapActivity, "Some problems occured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Publish an event in the database and add to the user
     */
    fun publishEvent(
        name: String,
        date: String,
        location: String,
        description: String,
        category: String) {
        val eid = System.currentTimeMillis().toString() + Random.nextInt()
        val e = Event(name, user.uid, category, description, location, date, eid)
        user.addEvent(e)
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        mDbRef.child("event").child(eid).setValue(e)
    }


    fun setToolBar(toolBar: Toolbar) {
        mapActivity.setSupportActionBar(toolBar)
    }

    fun printToast(msg: String) {
        Toast.makeText(mapActivity, msg, Toast.LENGTH_SHORT).show()
    }

}