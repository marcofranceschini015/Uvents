package com.example.uvents.model

/**
 * Class that represents a signed user
 * that could be also a publisher of events
 */
class User {

    lateinit var name: String
    lateinit var email: String
    lateinit var uid: String
    var categories: List<String> = listOf()
    var eventsPublished: List<Event> = listOf()
    private var followed: MutableMap<String, String> = mutableMapOf()
    private var eventsBooked: MutableMap<String, String> = mutableMapOf()

    constructor() {}

    constructor(name: String, email: String, uid: String) {
        this.name = name
        this.email = email
        this.uid = uid
    }

    /**
     * Return the list of events in string format
     * in a way to be used in the view
     */
    fun getEventNamePublished(): List<String> {
        val list: MutableList<String> = mutableListOf()
        eventsPublished.forEach { event ->
            list.add(event.name!!)
        }
        return list.toList()
    }


    /**
     * Return the list of followed users in string format
     * in a way to be used in the view
     */
    fun getFollowed(): Map<String, String> {
        return followed
    }


    /**
     * Get all the booked events map <eid, event name>
     */
    fun getEventsBooked(): Map<String, String> {
        return eventsBooked
    }


    fun setFollowed(map: Map<String, String>){
        followed = map.toMutableMap()
    }


    /**
     * Add an event to the list
     */
    fun addEvent(e: Event) {
        val eventList = eventsPublished.toMutableList()
        eventList.add(e)
        eventsPublished = eventList.toList()
    }


    /**
     * Add a new events booking by name
     */
    fun addBooking(eid: String, name: String) {
        eventsBooked[eid] = name
    }


    /**
     * Remove a booked events
     */
    fun removeBooking(eid: String) {
        eventsBooked.remove(eid)
    }


    /**
     * Get if an event is already booked or not
     */
    fun isBooked(eid: String): Boolean {
        return eventsBooked.containsKey(eid)
    }


    /**
     * Function to add a category to
     * the liked categories
     */
    fun addCategory(category: String) {
        val categoryList = categories.toMutableList()
        categoryList.add(category)
        categories = categoryList.toList()
    }


    /**
     * Add a user followed adding to the map
     * with uid and name
     */
    fun addFollow(uid: String, name: String) {
        followed[uid] = name
    }


    /**
     * Removed a followed user having the uid
     */
    fun removeFollow(uid: String) {
        followed.remove(uid)
    }


    /**
     * Check if a particular User is already followed
     */
    fun isFollowed(uid: String): Boolean {
        return followed.containsKey(uid)
    }

    /**
     * Remove a category from the liked
     * categories
     */
    fun removeCategory(category: String) {
        val categoryList = categories.toMutableList()
        categoryList.remove(category)
        categories = categoryList.toList()
    }


    /**
     * Get if a category is liked or not
     * by searching into the categories list
     */
    fun isFavouriteCategory(category: String): Boolean {
        return categories.toMutableList().contains(category)
    }
}