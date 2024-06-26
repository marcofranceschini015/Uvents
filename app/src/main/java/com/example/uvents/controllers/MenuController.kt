package com.example.uvents.controllers


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Telephony.Sms.Intents
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.uvents.R
import com.example.uvents.model.Event
import com.example.uvents.model.EventFetcher
import com.example.uvents.model.EventSearcher
import com.example.uvents.model.User
import com.example.uvents.ui.user.MapActivity
import com.example.uvents.ui.user.WelcomeActivity
import com.example.uvents.ui.user.menu_frgms.PersonalPageFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mapbox.maps.MapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Controller of all the activity connected with
 * the map view, all after the sign-in/up
 */
class MenuController(val mapActivity: MapActivity) {

    // manage the reading of events from db
    @RequiresApi(Build.VERSION_CODES.O)
    private var eventFetcher: EventFetcher = EventFetcher()

    // manage the annotations on the map
    private lateinit var annotationManager: AnnotationManager

    // instance of the actual User for ever app running
    private lateinit var user: User

    // variables for the database connection
    private lateinit var mDbRef: DatabaseReference


    /**
     * Set view the first time
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setView(mapView: MapView, fusedLocationProviderClient: FusedLocationProviderClient) {
        annotationManager = AnnotationManager(mapView, mapActivity, this, eventFetcher)
        annotationManager.addEventAnnotation()
        annotationManager.getCurrentLocation(fusedLocationProviderClient)
    }


    /**
     * Reset all the events by simply clear the db and
     * fetching again the db
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun resetView() {
        eventFetcher.clearEvents()
        eventFetcher.fetchEvents()
    }


    /**
     * Come to the WelcomeActivity as a logout
     */
    fun logout() {
        Toast.makeText(mapActivity, "Logout...", Toast.LENGTH_SHORT).show()
        val intent = Intent(mapActivity, WelcomeActivity::class.java)
        mapActivity.startActivity(intent)
        mapActivity.finish()
    }


    /**
     * Switch the fragment in the main map activity
     * (the one with the navbar)
     */
    fun switchFragment(f: Fragment) {
        mapActivity.replaceFragment(f)
    }


    /**
     * Check if I am the organizer of the event
     */
    fun ImtheOrganizer(organizerUid: String): Boolean {
        return organizerUid == user.uid
    }


    /**
     * Recover in the database the user from its uid
     */
    fun setUser(uid: String?) {
        mDbRef = FirebaseDatabase.getInstance(mapActivity.getString(R.string.firebase_url)).getReference()
        mDbRef.child("user").child(uid!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // recover the User by uid passed
                user = dataSnapshot.getValue(User::class.java)!!
                fetchEventsForUser()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    fun follow(uid: String, organizerName: String) {
        user.addFollow(uid, organizerName)
        printToast("Organizer followed")
        updateDatabase(user.categories, user.getFollowed(), user.getEventsBooked())
    }

    fun removeFollow(uid: String) {
        user.removeFollow(uid)
        printToast("Follow removed")
        updateDatabase(user.categories, user.getFollowed(), user.getEventsBooked())
    }

    fun isFollowed(uid: String): Boolean {
        return user.isFollowed(uid)
    }


    /**
     * Add a single event to the user published list
     * if the uid of the publisher is the same as the
     * actual user.uid
     */
    @RequiresApi(Build.VERSION_CODES.O)
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUser(categories: List<String>, events: List<String>, followed: Map<String, String>) {
        user.categories = categories
        user.setFollowed(followed)
        removeEvent(events) // todo send notification
        // recover the map
        updateDatabase(categories, followed, user.getEventsBooked())
    }


    /**
     * Add a new category in the user instance
     */
    fun addCategory(category: String) {
        user.addCategory(category)
        updateDatabase(user.categories, user.getFollowed(), user.getEventsBooked())
        printToast("Cateogry added")
    }


    /**
     * Remove a category from the liked of the
     * user
     */
    fun removeCategory(category: String) {
        user.removeCategory(category)
        updateDatabase(user.categories, user.getFollowed(), user.getEventsBooked())
        printToast("Category removed")
    }


    /**
     * Get if the category selected is from the
     * liked of a user
     */
    fun isFavouriteCategory(category: String): Boolean {
        return user.isFavouriteCategory(category)
    }


    /**
     * From a list of events' name remove the event
     * in the User db and in the event db
     */
    @RequiresApi(Build.VERSION_CODES.O)
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
        mDbRef = FirebaseDatabase.getInstance(mapActivity.getString(R.string.firebase_url)).getReference()
        val eventsRef = mDbRef.child("event")
        listEidToDelete.forEach { eid ->
            // remove from database given an eid
            eventsRef.child(eid.toString()).removeValue().addOnCompleteListener {}
            // remove from view annotation
            //annotationManager.removeSingleAnnotation(eid!!)
        }
    }


    /**
     * Update the database with the passed information
     */
    private fun updateDatabase(
        categories: List<String>,
        followed: Map<String, String>,
        eventsBooked: Map<String, String>) {
        // Reference to the specific user's node
        mDbRef = FirebaseDatabase.getInstance(mapActivity.getString(R.string.firebase_url)).getReference()
        val userRef = mDbRef.child("user").child(user.uid)


        // Map of data to update
        val updates = hashMapOf(
            "categories" to categories,
            "followed" to followed,
            "eventsBooked" to eventsBooked
        )

        // Update children of the user node
        userRef.updateChildren(updates).addOnCompleteListener {}
    }


    /**
     * Get if a name of an event is already taken
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun nameExists(name: String): Boolean{
        return eventFetcher.nameExists(name)
    }


    /**
     * Get if a location given from an address
     * exist or not
     */
    fun locationExist(address: String): Boolean {
        return annotationManager.locationExist(address)
    }


    /**
     * Publish an event in the database and add to the user
     */
    @RequiresApi(Build.VERSION_CODES.O)
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


    /**
     * Upload an image into firebase. Use coroutine and suspend fun
     * because need the other code to wait the finish of this fun
     */
    private suspend fun uploadImageToFirebase(fileUri: Uri, event: Event) {
        val fileName = fileUri.lastPathSegment ?: "default_name"
        val storageReference = FirebaseStorage.getInstance().getReference("uploads/$fileName")

        try {
            // Start the file upload and wait for it to finish
            val uploadTaskSnapshot = storageReference.putFile(fileUri).await()

            // Get the download URL and wait for it to be available
            val imageUrl = uploadTaskSnapshot.storage.downloadUrl.await().toString()
            event.imageUrl = imageUrl

            // Store the event into the db
            storeEvent(event)
        } catch (e: Exception) {
            // Handle possible exceptions such as network errors
            printToast("Upload failed: ${e.message}")
        }
    }


    /**
     * Store the event into the db under event node
     */
    private fun storeEvent(event: Event) {
        mDbRef = FirebaseDatabase.getInstance(mapActivity.getString(R.string.firebase_url)).getReference()
        mDbRef.child("event").child(event.eid!!).setValue(event)
    }


    /**
     * Set the tool bar in the map activity
     */
    fun setToolBar(toolBar: Toolbar) {
        mapActivity.setSupportActionBar(toolBar)
    }


    /**
     * useful to print toast messages
     */
    fun printToast(msg: String) {
        Toast.makeText(mapActivity, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Events not passed but recovered from firebase, this method apply all the filters chosen by the user
     * end return only the arrayList<String> of the filtered events
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun applyFilteredSearch(
        followed: Boolean,
        organizerName: String?,
        fromDate: String?,
        toDate: String?,
        fromTime: String?,
        checkedCategories: List<String>
    ) {
        val filteredEvent: List<Event>
        val fullEvents = eventFetcher.eventsData.value ?: listOf()
        val eventSearcher = eventFetcher.eventsData.value?.let { EventSearcher(it.toMutableList()) }
        filteredEvent = if (followed)
            eventSearcher!!.filteredSearch(user.getFollowed(), organizerName,fromDate,toDate,fromTime, checkedCategories)
        else
            eventSearcher!!.filteredSearch(emptyMap(), organizerName,fromDate,toDate,fromTime, checkedCategories)

        val eventsToRemove = getEventsToRemove(fullEvents, filteredEvent)

        // Remove from the actual list of events
        // the events removed
        eventFetcher.removeEvent(eventsToRemove.toMutableList())
    }


    /**
     * Get a list of eid of Events removed from a
     * List of Events compared to the local db
     */
    private fun getEventsToRemove(
        fullEvents: List<Event>,
        filteredEvents: List<Event>
    ): List<String?> {
        // Extract the list of IDs from the filtered events
        val filteredIds = filteredEvents.map { it.eid }.toSet()

        // Find all IDs that are not in the filtered list
        return fullEvents.filter { it.eid !in filteredIds }.map { it.eid }
    }


    /**
     * Book an event for the actual user
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun bookEvent(eid: String, name: String) {
        // Add to user
        user.addBooking(eid, name)

        // Add booking to the events
        eventFetcher.addBooking(eid, user.uid)

        updateDatabase(user.categories, user.getFollowed(), user.getEventsBooked())
        eventFetcher.updateEvent(eid)
    }


    /**
     * Get if an event by name is already booked
     * for the actual user
     */
    fun isBooked(eid: String): Boolean {
        return user.isBooked(eid)
    }


    /**
     * Remove the booking for the user for
     * the event eid
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun removeBook(eid: String) {
        user.removeBooking(eid)
        eventFetcher.removeBooking(eid, user.uid)

        // Update db
        updateDatabase(user.categories, user.getFollowed(), user.getEventsBooked())
        eventFetcher.updateEvent(eid)
    }


    fun getEventsBooked(): Map<String, String> {
        return user.getEventsBooked()
    }

}