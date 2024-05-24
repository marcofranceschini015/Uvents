package com.example.uvents.model

class Event {

    var name: String? = null
//    var organizer: Organizer? = null
    var organizer: String? = null
    var category: String? = null
    var descruption: String? = null
    var address: String? = null

    constructor() {}

    constructor(name: String?, organizer: String?, category: String?, description: String?, address: String?){
        this.name = name
        this.organizer = organizer
        this.category = category
        this.descruption = description
        this.address = address
    }
}