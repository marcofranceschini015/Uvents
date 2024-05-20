package com.example.uvents.model

class Organizer {
    var companyName: String? = null
    var email: String? = null
    var uid: String? = null
    lateinit var eventsPublished: ArrayList<Event>

    constructor() {}

    constructor(companyName: String?, email: String?, uid: String?){
        this.companyName = companyName
        this.email = email
        this.uid = uid
        eventsPublished = ArrayList<Event>()
    }

    constructor(companyName: String?, email: String?, uid: String?, events: ArrayList<Event>){
        this.companyName = companyName
        this.email = email
        this.uid = uid
        eventsPublished = events
    }
}