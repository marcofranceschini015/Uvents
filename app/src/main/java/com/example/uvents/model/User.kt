package com.example.uvents.model

import com.google.firebase.database.Exclude

class User {

    lateinit var name: String
    lateinit var email: String
    lateinit var uid: String
    lateinit var categories: List<String>

    constructor() {}

    constructor(name: String, email: String, uid: String){
        this.name = name
        this.email = email
        this.uid = uid
    }
}