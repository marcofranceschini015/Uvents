package com.example.uvents.controllers

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.uvents.model.User
import com.example.uvents.ui.user.WelcomeActivity
import com.example.uvents.ui.user.fragments.WelcomeFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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


    /**
     * Function to signUp and create the User then added to a db
     */
    fun signUp(name: String, email: String, password: String, isOrganizer: Boolean){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(welcomeActivity) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name, email, auth.currentUser?.uid!!)
                    Toast.makeText(welcomeActivity, "User added", Toast.LENGTH_SHORT).show()
                    welcomeActivity.replaceFragment(WelcomeFragment(this, true))
                    showViewWelcomeSignIn(name)
                } else {
                    Toast.makeText(welcomeActivity, "Problems during sign-up", Toast.LENGTH_SHORT).show()
                }
            }
    }


    /**
     * Add the user to db
     */
    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance("https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
        mDbRef.child("user").child(uid).setValue(User(name, email, uid))
    }


    /**
     * Sign in function and set username in the text view
     *
     * @param isOrganizer different sign in if is one organizer
     */
    fun signIn(email: String, password: String, isOrganizer: Boolean) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(welcomeActivity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(welcomeActivity, "Sign-in successful", Toast.LENGTH_SHORT).show()
                    welcomeActivity.replaceFragment(WelcomeFragment(this, true))

                    // Set username in the view
                    mDbRef = FirebaseDatabase.getInstance("https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                    mDbRef.child("user").child(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // Get User object and use the values to update the UI
                            val user = dataSnapshot.getValue(User::class.java)
                            if (user != null) {
                                showViewWelcomeSignIn(user.name!!)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                } else {
                    Toast.makeText(welcomeActivity, "Problems during sign-in", Toast.LENGTH_SHORT).show()
                }
            }
    }


    /**
     * get the location of a client and then switch the activity
     */
    fun getLocation() {
        // check permission
        if (ActivityCompat.checkSelfPermission(welcomeActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(welcomeActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(welcomeActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
                welcomeActivity.goToMap(it.latitude.toString(), it.longitude.toString())
            } else {
                Toast.makeText(welcomeActivity, "problem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Modify view to show the username
     */
    private fun showViewWelcomeSignIn(username: String){
        welcomeActivity.showUsername(username)
    }

}