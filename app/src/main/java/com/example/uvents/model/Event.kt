package com.example.uvents.model

class Event {

    var name: String? = null
//    var organizer: Organizer? = null
    var organizer: User? = null
    var organizerFake: String? = null
    var category: String? = null
    var description: String? = null
    var address: String? = null
    var date: String? = null

    constructor() {}

    constructor(name: String?, organizer: User?, category: String?, description: String?, address: String?, date: String?){
        this.name = name
        this.organizer = organizer
        this.category = category
        this.description = description
        this.address = address
        this.date = date
    }

    constructor(name: String?, organizer: String?, category: String?, description: String?, address: String?){
        this.name = name
        this.organizerFake = organizer
        this.category = category
        this.description = description
        this.address = address
    }
}