package com.example.uvents.controllers

import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.example.uvents.MapActivity
import com.example.uvents.model.User
import com.example.uvents.ui.WelcomeActivity
import com.example.uvents.ui.fragments.SignUpFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WelcomeController(private val welcomeActivity: WelcomeActivity) {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mDbRef: DatabaseReference
    private var fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(welcomeActivity)

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

    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(welcomeActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(welcomeActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(welcomeActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        var location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
//                val textLatitude = "Latitude: "+it.latitude.toString()
//                val textLongitude = "Longitude: "+it.longitude.toString()
//                latitude.text = textLatitude
//                longitude.text = textLongitude
//                Toast.makeText(welcomeActivity, "successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(welcomeActivity, MapActivity::class.java)
                intent.putExtra("Latitude", it.latitude.toString())
                intent.putExtra("Longitude", it.longitude.toString())
                welcomeActivity.startActivity(intent)
                welcomeActivity.finish()
            } else {
                Toast.makeText(welcomeActivity, "problem", Toast.LENGTH_SHORT).show()
            }
        }


    }

}