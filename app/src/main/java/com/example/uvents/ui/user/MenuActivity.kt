package com.example.uvents.ui.user

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.uvents.R
import com.example.uvents.controllers.MenuController
import com.example.uvents.ui.user.menu_frgms.BookFragment
import com.example.uvents.ui.user.menu_frgms.ChatsFragment
import com.example.uvents.ui.user.menu_frgms.MapFragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
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

        // set up the navigation menu
        bottomNavigation = findViewById(R.id.bottomNav)

        // create the controller and set the user
        // relative to the uid of the login
        menuController = MenuController(this@MenuActivity)
        menuController.setUser(intent.getStringExtra("uid"))

        // create the mapFragment and set it
        mapFragment = MapFragment(menuController)
        replaceFragment(mapFragment)

        addBadgeToChatIcon()
        runBlocking {
            menuController.readUserTotalNewMessages(
                intent.getStringExtra("uid")!!,
                onSuccess = {
                    updateBadgeCount(it!!)
                },
                onError = { exception ->
                    println("Failed to read value: ${exception.message}")
                }
            )
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
                    menuController.removeOldBooking()
                    replaceFragment(BookFragment(menuController, menuController.getEventsBooked()))
                    true
                }

                R.id.chat -> {
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