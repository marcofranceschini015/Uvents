package com.example.uvents.controllers

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uvents.model.User
import com.example.uvents.ui.user.WelcomeActivity
import com.example.uvents.ui.user.welcome_frgms.CategorySelectionFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class WelcomeController(val welcomeActivity: WelcomeActivity) {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mDbRef: DatabaseReference
    private val dbUrl: String = "https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/"
    private lateinit var user: User

    /**
     * Communicate with the welcome activity to switch fragment
     */
    fun switchFragment(f: Fragment){
        welcomeActivity.replaceFragment(f)
    }

    /**
     * Function to signUp and create the User then added to a db
     */
    fun signUp(name: String, email: String, password: String){
        // first check if the email already exists
        emailExist(email) { exists ->
            if (exists) {
                Toast.makeText(welcomeActivity, "This email is already taken", Toast.LENGTH_SHORT).show()
            } else { // if free to use continue with the signup
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(welcomeActivity) { task ->
                        if (task.isSuccessful) {
                            user = User(name, email, auth.currentUser?.uid!!)
                            // go to the selection of categories
                            switchFragment(CategorySelectionFragment(this))
                        } else {
                            Toast.makeText(welcomeActivity, "Wrong email or password too short", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }


    /**
     * Finalize the user by adding the liked
     * categories selected
     */
    fun initializeUser(categories: List<String>){
        user.categories = categories
        addUserToDatabase()
        Toast.makeText(welcomeActivity, "User added", Toast.LENGTH_SHORT).show()
        welcomeActivity.goToYourLocalizationMap(user.uid)
    }


    /**
     * Add the user to db
     */
    private fun addUserToDatabase() {
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
        mDbRef.child("user").child(user.uid).setValue(user)
        mDbRef.child("user").child(user.uid).child("totalNewMsg").setValue(0)
        mDbRef.child("user").child(user.uid).child("NumEventsDeleted").setValue(0)
    }


    /**
     * Sign in function
     */
    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(welcomeActivity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(welcomeActivity, "Sign-in successful", Toast.LENGTH_SHORT).show()
                    mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()
                    mDbRef.child("user").child(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            user = dataSnapshot.getValue(User::class.java)!! // set user
                            welcomeActivity.saveCredentials(email, password)
                            welcomeActivity.goToYourLocalizationMap(user.uid)
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                } else {
                    Toast.makeText(welcomeActivity, "Wrong email or password", Toast.LENGTH_SHORT).show()
                }
            }
    }


    /**
     * Function to check if an email already exists in the entire database
     */
    private fun emailExist(email: String, callback: (exists: Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance(dbUrl)
        val userRef = database.getReference("user")

        // Helper function to query a node
        fun queryNode(ref: DatabaseReference, onCompleted: (Boolean) -> Unit) {
            ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onCompleted(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Database error: ${error.message}")
                    onCompleted(false)
                }
            })
        }

        // Query both nodes and aggregate the results
        var checkCount = 0
        var emailFound = false

        val handleResult: (Boolean) -> Unit = { exists ->
            if (exists) emailFound = true
            checkCount++
            if (checkCount == 1) { // Ensure the query has completed
                callback(emailFound)
            }
        }

        queryNode(userRef, handleResult)
    }


    /**
     * Hide the back arrow when you are into the
     * category choice phase
     */
    fun hideBackArrow(){
        welcomeActivity.hideBack()
    }

    fun showBackArrow() {
        welcomeActivity.showBack()
    }
}
