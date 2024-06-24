package com.example.uvents.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.uvents.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


/**
 * Class that is useful to manage all the saved events
 * into a database
 */
class EventFetcher {
    var eventsData = MutableLiveData<MutableList<Event>>()
    private val dbUrl: String = "https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/"


    init {
        fetchEvents()
    }


    /**
     * Fetch all the events the first time that the map is open
     * by reading from the database
     */
    fun fetchEvents() {
        val dbRef = FirebaseDatabase.getInstance(dbUrl).getReference("event")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                eventsData.postValue(events.toMutableList())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EventFetcher", "Database error: $error.message")
            }
        })
    }


    /**
     * Add a new event into the data
     */
    fun addEvent(e: Event) {
        eventsData.value?.add(e)
    }


    /**
     * Remove events published from a list of eid
     */
    fun removeEvent(listEid: MutableList<String?>) {
        val eventsToRemove = mutableListOf<Event>()
        eventsData.value?.forEach { e ->
            if (listEid.contains(e.eid)) {
                eventsToRemove.add(e)
            }
        }
        eventsToRemove.forEach { e ->
            eventsData.value?.remove(e)
        }
    }


    /**
     * Clear all the events in the local db
     */
    fun clearEvents() {
        eventsData = MutableLiveData<MutableList<Event>>()
    }


    /**
     * Get the event from an eid
     */
    fun addBooking(eid: String, uid: String) {
        eventsData.value?.forEach { e ->
            if (e.eid == eid)
                e.addBooking(uid)
        }
    }


    /**
     * Remove the uid booked for the event with eid
     */
    fun removeBooking(eid: String, uid: String) {
        eventsData.value?.forEach { e ->
            if (e.eid == eid)
                e.removeBooking(uid)
        }
    }


    /**
     * Get the booking of an event by eid
     */
    private fun getBooking(eid: String): List<String> {
        eventsData.value?.forEach { e ->
            if (e.eid == eid)
                return e.getUidBooked()
        }
        return listOf()
    }


    /**
     * Check if a name is already taken
     */
    fun nameExists(name: String): Boolean {
        eventsData.value?.forEach { e->
            if (e.name.equals(name))
                return true
        }
        return false
    }


    /**
     * Update the database with the passed information
     */
    fun updateEvent(
        eid: String) {
        // Reference to the specific event's node
        val mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        val userRef = mDbRef.child("event").child(eid)
        val uidBooked = getBooking(eid)

        // Map of data to update
        val updates = hashMapOf(
            "uidBooked" to uidBooked
        )

        // Update children of the event node
        userRef.updateChildren(updates as Map<String, Any>).addOnCompleteListener {}
    }

}