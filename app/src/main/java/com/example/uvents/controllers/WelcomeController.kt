package com.example.uvents.controllers

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.example.uvents.model.User
import com.example.uvents.ui.WelcomeActivity
import com.example.uvents.ui.fragments.SignUpFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WelcomeController(private val welcomeActivity: WelcomeActivity) {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mDbRef: DatabaseReference

    /**
     * Communicate with the welcome activity to switch fragment
     */
    fun switchFragment(f: Fragment){
        welcomeActivity.replaceFragment(f)
    }


    fun signUp(name: String, email: String, password: String, isOrganizer: Boolean){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(welcomeActivity) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name, email, auth.currentUser?.uid!!)
                    Toast.makeText(welcomeActivity, "User added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(welcomeActivity, "Some problems occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance("https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
        mDbRef.child("user").child(uid).setValue(User(name, email, uid))
    }

}