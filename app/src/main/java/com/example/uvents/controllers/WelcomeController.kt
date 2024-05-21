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
     *
     * @param isOrganizer if true sign up an organizer and go to his view
     */
    fun signUp(name: String, email: String, password: String, isOrganizer: Boolean){
        // todo check email already exist

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(welcomeActivity) { task ->
                if (task.isSuccessful) {
                    if (isOrganizer){
                        addOrganizerToDatabase(name, email, auth.currentUser?.uid!!)
                        Toast.makeText(welcomeActivity, "Organizer added", Toast.LENGTH_SHORT).show()
                        welcomeActivity.goToOrganizerView()
                    } else {
                        addUserToDatabase(name, email, auth.currentUser?.uid!!)
                        Toast.makeText(welcomeActivity, "User added", Toast.LENGTH_SHORT).show()
                        welcomeActivity.replaceFragment(WelcomeFragment(this, true))
                        welcomeActivity.showUsername(name)
                    }
                } else {
                    Toast.makeText(welcomeActivity, "Problems during sign-up", Toast.LENGTH_SHORT).show()
                }
            }
    }


    /**
     * Add a new organizer to a database
     */
    private fun addOrganizerToDatabase(name: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance("https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
        mDbRef.child("organizer").child(uid).setValue(User(name, email, uid))
    }


    /**
     * Add the user to db
     */
    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance("https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
        mDbRef.child("user").child(uid).setValue(User(name, email, uid))
    }


    /**
     * Sign in function
     * If is organizer go to organizer view
     * If is user go to welcome and set username in the text view
     */
    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(welcomeActivity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(welcomeActivity, "Sign-in successful", Toast.LENGTH_SHORT).show()
                    switchedSignIn()
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
     * Go after the sign in page differently if is organizer or not
     */
    private fun switchedSignIn(){
        mDbRef = FirebaseDatabase.getInstance("https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
        mDbRef.child("user").child(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // if read an user with that uid
                if (dataSnapshot.exists()){
                    welcomeActivity.replaceFragment(WelcomeFragment(this@WelcomeController, true))
                    // show username
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        welcomeActivity.showUsername(user.name!!)
                    }
                } else { // else is an organizer
                    welcomeActivity.goToOrganizerView()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}
