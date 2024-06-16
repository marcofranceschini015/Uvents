package com.example.uvents.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class EventFetcher {
    val eventsData = MutableLiveData<List<Event>>()
    private val dbUrl: String = "https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/"


    init {
        fetchEvents()
    }


    fun fetchEvents() {
        // Assume FirebaseDatabase setup and fetching logic
        val dbRef = FirebaseDatabase.getInstance(dbUrl).getReference("event")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                eventsData.postValue(events)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EventFetcher", "Database error: $error.message")
            }
        })
    }

}