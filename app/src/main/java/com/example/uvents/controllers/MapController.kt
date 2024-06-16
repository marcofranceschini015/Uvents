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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.random.Random

/**
 * Controller of all the activity connected with
 * the map view, all after the sign-in/up
 */
class MapController(val mapActivity: MapActivity) {

    private var eventFetcher: EventFetcher = EventFetcher()
    private lateinit var annotationManager: AnnotationManager
    // instance of the actual User for ever app running
    private lateinit var user: User

    // variables for the database connection
    private val dbUrl: String =
        "https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/"
    private lateinit var mDbRef: DatabaseReference


    fun setView(mapView: MapView, fusedLocationProviderClient: FusedLocationProviderClient) {
        annotationManager = AnnotationManager(mapView, mapActivity, this, eventFetcher)
        annotationManager.addEventAnnotation()
        annotationManager.getCurrentLocation(fusedLocationProviderClient)
    }


    /**
     * Switch the fragment in the main map activity
     * (the one with the navbar)
     */
    fun switchFragment(f: Fragment) {
        mapActivity.replaceFragment(f)
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
                fetchEventsForUser()
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
    private fun fetchEventsForUser() {
        val userEvents = mutableListOf<Event>()
        eventFetcher.eventsData.observe(mapActivity, Observer { listevent->
            listevent.forEach{ e->
                if (e.uid == user.uid){
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

        // remove from the db of events
        eventFetcher.removeEvent(listEidToDelete)

        // remove into db
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        val eventsRef = mDbRef.child("event")
        listEidToDelete.forEach { eid ->
            // remove from database given an eid
            eventsRef.child(eid.toString()).removeValue().addOnCompleteListener {}
            // remove from view annotation
            annotationManager.removeSingleAnnotation(eid!!)
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


    fun nameExists(name: String): Boolean{
        return eventFetcher.nameExists(name)
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
        CoroutineScope(Dispatchers.IO).launch {
            uploadImageToFirebase(fileUri, e)
            withContext(Dispatchers.Main) {
                // Update UI or handle the event further in the UI thread
                // add event to user instance
                user.addEvent(e)

                // add to the event fetcher
                eventFetcher.addEvent(e)

                // add the annotation to the map
                annotationManager.addSingleAnnotation(e)

                // reload the personal page after publish an event
                setPersonalPage()
            }
        }
    }


    private suspend fun uploadImageToFirebase(fileUri: Uri, event: Event) {
        val fileName = fileUri.lastPathSegment ?: "default_name"
        val storageReference = FirebaseStorage.getInstance().getReference("uploads/$fileName")

        try {
            // Start the file upload and wait for it to finish
            val uploadTaskSnapshot = storageReference.putFile(fileUri).await()

            // Get the download URL and wait for it to be available
            val imageUrl = uploadTaskSnapshot.storage.downloadUrl.await().toString()
            event.imageUrl = imageUrl

            storeEvent(event) // Assume this function is adapted for coroutine use or properly handles database operations
        } catch (e: Exception) {
            // Handle possible exceptions such as network errors
            Toast.makeText(mapActivity, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            ""
        }
    }


    private fun storeEvent(event: Event) {
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        mDbRef.child("event").child(event.eid!!).setValue(event)
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