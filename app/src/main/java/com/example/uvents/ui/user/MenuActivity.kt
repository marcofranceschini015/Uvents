package com.example.uvents.ui.user

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.MenuController
import com.example.uvents.ui.user.menu_frgms.BookFragment
import com.example.uvents.ui.user.menu_frgms.ChatsFragment
import com.example.uvents.ui.user.menu_frgms.MapFragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.runBlocking

/**
 * Activity with the map and every events on it
 * There is also the menu bar that is permanent
 * when the fragments will be switched.
 * The menu leads to Personal page, Home, Chat
 * and Booked events
 */
class MenuActivity : AppCompatActivity() {

    private lateinit var menuController: MenuController
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var mapFragment: MapFragment

    /**
     * On creation create a mapController and recover the user in it
     * then launch the fragment with the map, by filling it
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
//        }

        // set up the navigation menu
        bottomNavigation = findViewById(R.id.bottomNav)

        // create the controller and set the user
        // relative to the uid of the login
        menuController = MenuController(this)
        menuController.setUser(intent.getStringExtra("uid"))

        // create the mapFragment and set it
        mapFragment = MapFragment(menuController)
        replaceFragment(mapFragment)

        addBadgeToChatIcon()
        runBlocking {
            menuController.readUserTotalNewMessages(intent.getStringExtra("uid")!!)
                ?.let { updateBadgeCount(it) }
        }

        // set up the listener for every
        // icon in the menu, in a way to manage
        // the switch between every fragment
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    backHome()
                    true
                }

                R.id.ticket -> {
                    replaceFragment(BookFragment(menuController, menuController.getEventsBooked()))
                    true
                }

                R.id.chat -> {
                    updateBadgeCount(0)
//                    val chatManager = ChatManager(getString(R.string.firebase_url))
//                    val userUid = chatManager.getCurrentUid()
//                    chatManager.updateUserTotalNewMessages(userUid, false)
                    replaceFragment(ChatsFragment(menuController))
                    true
                }

                R.id.profile -> {
                    menuController.setPersonalPage()
                    true
                }

                else -> false
            }
        }

//        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                PERMISSIONS_REQUEST_LOCATION
//            )
//        }
    }

    fun backHome() {
        replaceFragment(mapFragment)
    }


    /**
     * Function that replace the fragment in the activity
     */
    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frgMapContainer, fragment)
        fragmentTransaction.commit()
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 100) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Il permesso è stato concesso, avvia gli aggiornamenti della posizione
////                replaceFragment(mapFragment)
//            } else {
//                // Il permesso è stato negato, gestisci il caso di negazione
////                replaceFragment(mapFragment)
//            }
//        }
//    }


//    private companion object {
//
//        private const val PERMISSIONS_REQUEST_LOCATION = 0
//
//        fun Context.isPermissionGranted(permission: String): Boolean {
//            return ContextCompat.checkSelfPermission(
//                this, permission
//            ) == PackageManager.PERMISSION_GRANTED
//        }
//    }

    private fun addBadgeToChatIcon() {
        val badge = BadgeDrawable.create(this).apply {
            number = 0
            isVisible = false
        }
        bottomNavigation.getOrCreateBadge(R.id.chat).apply {
            number = badge.number
            isVisible = badge.isVisible
        }
    }

    private fun updateBadgeCount(count: Int) {
        val badge = bottomNavigation.getOrCreateBadge(R.id.chat)
        badge.number = count
        badge.isVisible = count > 0
    }
}