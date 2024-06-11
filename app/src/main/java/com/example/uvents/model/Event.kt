package com.example.uvents.model

class Event {

    var name: String = ""
    var uid: String? = null
    var organizerName: String? = null
    var category: String? = null
    var description: String? = null
    var address: String? = null
    var date: String? = null
    var eid: String? = null

    constructor() {}

    constructor(name: String?, uid: String?, organizerName: String?,category: String?, description: String?, address: String?, date: String?, eid: String?){
        this.name = name
        this.uid = uid
        this.organizerName = organizerName
        this.category = category
        this.description = description
        this.address = address
        this.date = date
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
}