package com.example.uvents.controllers

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.net.Uri
import android.text.Editable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.uvents.R
import com.example.uvents.model.Event
import com.example.uvents.model.EventFetcher
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
import com.google.firebase.storage.FirebaseStorage
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

/**
 * Controller of all the activity connected with
 * the map view, all after the sign-in/up
 */
class MapController(val mapActivity: MapActivity) {

    private var eventFetcher: EventFetcher = EventFetcher()
    private lateinit var mapView: MapView

    // instance of the actual User for ever app running
    private lateinit var user: User

    // variables for the database connection
    private val dbUrl: String =
        "https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/"
    private lateinit var mDbRef: DatabaseReference


    fun attachView(mapView: MapView) {
        this.mapView = mapView
        addEventAnnotation()
    }


    /**
     * Switch the fragment in the main map activity
     * (the one with the navbar)
     */
    fun switchFragment(f: Fragment) {
        mapActivity.replaceFragment(f)
    }


    /**
     * Get the current location, set the pointer on the
     * position, and locate the camera around the position
     */
    fun getCurrentLocation( //todo da tagliare assolutamente
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
                //addEventAnnotation(mapView)
            } else {
                // otherwise set the camera to a default value (Milan)
                val cameraPosition =
                    CameraOptions.Builder().center(Point.fromLngLat(9.188120, 45.463619)).zoom(14.0)
                        .build()
                mapView.mapboxMap.setCamera(cameraPosition)

                addAnnotationToMap(null, null)
                //addEventAnnotation(mapView)
            }
        }
    }


    /**
     * Add an annotation for every events published
     */
    private fun addEventAnnotation() {
        eventFetcher.eventsData.observe(mapActivity, Observer { listevent ->
            listevent.forEach { event ->
                val geocoder = Geocoder(mapActivity, Locale.getDefault())
                val addresses = event.address
                    .let {
                        geocoder.getFromLocationName(it!!, 1)
                    }
                var longitude = 0.0
                var latitude = 0.0
                if (addresses!!.isEmpty()) {
                    Toast.makeText(mapActivity, "Location inexistent or not found", Toast.LENGTH_LONG)
                        .show()
                } else {
                    val address = addresses[0]
                    longitude = address.longitude
                    latitude = address.latitude
                }

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
                    val viewAnnotationManager = mapView.viewAnnotationManager
                    val viewAnnotation = viewAnnotationManager.addViewAnnotation(
                        resId = R.layout.item_callout_view,
                        options = viewAnnotationOptions {
                            geometry(Point.fromLngLat(longitude, latitude))
                        }
                    )
                    viewAnnotation.findViewById<TextView>(R.id.annotation).text = event.name

                    // set up a click listener for every event
                    pointAnnotationManager.apply {
                        addClickListener(
                            OnPointAnnotationClickListener { clickedAnnotation ->
                                if (pointAnnotation == clickedAnnotation) {
                                    // attenzione, togliere lo user
                                    mapActivity.replaceFragment(
                                        EventFragment(
                                            this@MapController,
                                            event.name!!,
                                            event.organizerName!!,
                                            event.category!!,
                                            event.date!!,
                                            event.time!!,
                                            event.description!!,
                                            event.address!!,
                                            event.imageUrl!!
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
                fetchEvents()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    /**
     * Add a single event to the user published list
     * if the uid of the publisher is the same as the
     * actual user.uid
     */
    private fun fetchEvents() {
        val userEvents = mutableListOf<Event>()
        eventFetcher.eventsData.observe(mapActivity, Observer { listevent->
            listevent.forEach{ e->
                if (e.uid == user.uid && !userEvents.contains(e)){
                    userEvents.add(e)
                }
            }
            user.eventsPublished = userEvents.toList()
        })
    }


    /**
     * Set up the personal page with all information in the view
     */
    fun setPersonalPage() {
        mapActivity.replaceFragment(
            PersonalPageFragment(
                this,
                user.name,
                user.email,
                user.categories,
                user.getEventNamePublished(),
                user.getFollowed()
            )
        )
    }


    /**
     * Update a user when modifying the personal page
     * with all the lists recovered from the page
     */
    fun updateUser(categories: List<String>, events: List<String>, followed: List<String>) {
        // todo organizer not followed -> modify view
        user.categories = categories
        removeEvent(events) // todo send notification
        updateDatabase(categories, user.getFollowed())
    }


    fun addCategory(category: String) {
        user.addCategory(category)
        updateDatabase(user.categories, user.getFollowed())
    }

    fun removeCategory(category: String) {
        user.removeCategory(category)
        updateDatabase(user.categories, user.getFollowed())
    }

    fun isFavouriteCategory(category: String): Boolean {
        return user.isFavouriteCategory(category)
    }


    /**
     * From a list of events' name remove the event
     * in the User db and in the event db
     */
    private fun removeEvent(events: List<String>) {
        // got the eid to delete
        val listEidToDelete = mutableListOf<String?>()
        user.eventsPublished.forEach { event ->
            if (!events.contains(event.name)) {
                listEidToDelete.add(event.eid)
            }
        }

        // delete into User instance
        user.eventsPublished = user.eventsPublished.filter { it.eid !in listEidToDelete }

        // remove into db
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        val eventsRef = mDbRef.child("event")
        listEidToDelete.forEach { eid ->
            eventsRef.child(eid.toString()).removeValue().addOnCompleteListener {}
        }
    }


    /**
     * Update the database with the passed information
     */
    private fun updateDatabase(categories: List<String>, followed: List<String>) {
        // Reference to the specific user's node
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        val userRef = mDbRef.child("user").child(user.uid)


        // Map of data to update
        val updates = hashMapOf<String, Any>(
            "categories" to categories
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
        time: String,
        location: String,
        description: String,
        category: String,
        fileUri: Uri
    ) {
        // create an eid unique
        val eid = System.currentTimeMillis().toString() + Random.nextInt()
        val e = Event(name, user.uid, user.name, category, description, location, date, time, eid)

        // convert image to url and then
        // save the event into the db
        uploadImageToFirebase(fileUri, e)

        // add event to user instance
        user.addEvent(e)
    }


    private fun uploadImageToFirebase(fileUri: Uri, event: Event) {
        val fileName = fileUri.lastPathSegment ?: "default_name"
        val storageReference = FirebaseStorage.getInstance().getReference("uploads/$fileName")

        storageReference.putFile(fileUri)
            .addOnSuccessListener {
                // Get the download URL
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    event.imageUrl = imageUrl
                    storeEvent(event)
                    //saveImageUrlToEvent(imageUrl, eventKey)
                }
            }
            .addOnFailureListener {
                Toast.makeText(mapActivity, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeEvent(event: Event) {
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        mDbRef.child("event").child(event.eid!!).setValue(event)
    }


    /**
     * Modify the map View adding the annotations for every events
     * updating in case of new events published
     */
    fun updateMapViewWithEvents() {
        eventFetcher.fetchEvents()
        addEventAnnotation()
    }


    fun setToolBar(toolBar: Toolbar) {
        mapActivity.setSupportActionBar(toolBar)
    }


    fun printToast(msg: String) {
        Toast.makeText(mapActivity, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Events not passed but recovered from firebase, this method apply all the filters chosen by the user
     * end return only the arraList<String> of the filtered events
     */
    fun applyFilteredSearch(
        events: ArrayList<Event>,
        organizerName: Editable?,
        fromDate: CharSequence,
        toDate: CharSequence,
        fromTime: Editable?,
        toTime: Editable?,
        checkedCategories: List<String>
    ): ArrayList<Event> {

        val filteredEvents: ArrayList<Event> = arrayListOf<Event>()

        events.forEach { event ->
            if (organizerName.toString() == event.organizerName) {
                filteredEvents.add(event)
            }

            // todo date filter

            // todo time filter

            if (checkedCategories.contains(event.category)) {
                filteredEvents.add(event)
            }
        }
        return filteredEvents
    }

}