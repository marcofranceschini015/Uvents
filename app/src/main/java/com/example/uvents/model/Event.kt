package com.example.uvents.model

class Event {

    var name: String? = null
//    var organizer: Organizer? = null
var organizer: String? = null
    var categories: ArrayList<String> = ArrayList()
    var descruption: String? = null
    var address: String? = null

    constructor() {}

    constructor(name: String?, organizer: String?, categories: ArrayList<String>, description: String?, address: String?){
        this.name = name
        this.organizer = organizer
        this.categories = categories
        this.descruption = description
        this.address = address
    }
}