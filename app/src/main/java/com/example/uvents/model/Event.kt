package com.example.uvents.model

class Event {

    var name: String = ""
//    var organizer: Organizer? = null
    var organizer: User? = null
    var organizerFake: String = ""
    var category: String = ""
    var description: String = ""
    var address: String = ""
    var date: String = ""
    //add var time

    constructor() {}

    constructor(name: String, organizer: User?, category: String, description: String, address: String, date: String){
        this.name = name
        this.organizer = organizer
        this.category = category
        this.description = description
        this.address = address
        this.date = date
    }

    constructor(name: String, organizer: String, category: String, date: String, description: String, address: String){
        this.name = name
        this.organizerFake = organizer
        this.category = category
        this.description = description
        this.address = address
        this.date = date
    }
}