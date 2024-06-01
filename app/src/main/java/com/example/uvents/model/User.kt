package com.example.uvents.model

import com.google.firebase.database.Exclude

class User {

    lateinit var name: String
    lateinit var email: String
    lateinit var uid: String
    lateinit var categories: List<String>
    private var eventsPublished: List<Event> = listOf()

    constructor() {}

    constructor(name: String, email: String, uid: String){
        this.name = name
        this.email = email
        this.uid = uid
    }

    /**
     * Return the list of events in string format
     * in a way to be used in the view
     */
    fun getEventsPublished():List<String> {
        val list: MutableList<String> = mutableListOf()
        eventsPublished.forEach { event ->
            list.add(event.name!!)
        }
        return list.toList()
    }
}