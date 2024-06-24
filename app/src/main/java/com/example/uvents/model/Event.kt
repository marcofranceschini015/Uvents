package com.example.uvents.model

/**
 * Class that represents an event
 * with every information relative to it
 */
class Event {
    var name: String? = ""
    var uid: String? = null
    var organizerName: String? = null
    var category: String? = null
    var description: String? = null
    var address: String? = null
    var date: String? = null
    var time: String? = null
    var eid: String? = null
    var imageUrl: String? = null
    private var uidBooked: List<String> = listOf()

    constructor() {}

    constructor(name: String?, uid: String?, organizerName: String?,category: String?, description: String?, address: String?, date: String?, time: String?, eid: String?){
        this.name = name
        this.uid = uid
        this.organizerName = organizerName
        this.category = category
        this.description = description
        this.address = address
        this.date = date
        this.time = time
        this.eid = eid
    }

    constructor(name: String, organizer: String, category: String, date: String, description: String, address: String){
        this.name = name
        this.uid = organizer
        this.category = category
        this.description = description
        this.address = address
        this.date = date
    }


    /**
     * Add a new booked user by uid
     */
    fun addBooking(uid: String) {
        val booking = uidBooked.toMutableList()
        booking.add(uid)
        uidBooked = booking.toList()
    }


    /**
     * Remove an user booked to this event
     */
    fun removeBooking(uid: String) {
        val booking = uidBooked.toMutableList()
        booking.remove(uid)
        uidBooked = booking.toList()
    }


    fun getUidBooked(): List<String> {
        return uidBooked
    }
}