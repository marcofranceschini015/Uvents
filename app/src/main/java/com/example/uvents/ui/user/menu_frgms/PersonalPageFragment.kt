package com.example.uvents.ui.user.menu_frgms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.example.uvents.controllers.adapter.PersonalPageAdapter
import com.example.uvents.ui.user.menu_frgms.PublishEventFragment

/**
 * Fragment that show the Personal Page content
 * with all the user's info
 */
class PersonalPageFragment(
    private val mapController: MapController,
    private val username: String,
    private val email: String,
    private val categories: List<String>,
    private val events: List<String>,
    private val followed: List<String>
) : Fragment() {

    // View elements
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var rvCategories: RecyclerView
    private lateinit var rvEvents: RecyclerView
    private lateinit var rvFollowed: RecyclerView
    private lateinit var btnPublish: Button
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button
    private lateinit var tvEvents: TextView
    private lateinit var tvFollowed: TextView

    // liked categories recycler view variables
    private lateinit var adapterCategories: PersonalPageAdapter
    private lateinit var copyCategories: MutableList<String>

    // events published recycler view variables
    private lateinit var adapterEvents: PersonalPageAdapter
    private lateinit var copyEvents: MutableList<String>

    // followed organizers recycler view variables
    private lateinit var adapterFollowed: PersonalPageAdapter
    private lateinit var copyFollowed: MutableList<String>


    /*+
    On the creation of the view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_personal_page, container, false)

        if (v != null) {
            // link view
            tvUsername = v.findViewById(R.id.tvUsername)
            tvEmail = v.findViewById(R.id.tv_email)
            tvEvents = v.findViewById(R.id.tvEvents)
            tvFollowed = v.findViewById(R.id.tvFollowed)
            rvCategories = v.findViewById(R.id.rv_categories)
            rvEvents = v.findViewById(R.id.rv_events)
            rvFollowed = v.findViewById(R.id.rv_followed)
            btnPublish = v.findViewById(R.id.btnPublish)
            btnSave = v.findViewById(R.id.btnSave)
            btnLogout = v.findViewById(R.id.btnLogout)

            // recycler view categories
            copyCategories = categories.toMutableList()
            adapterCategories = PersonalPageAdapter(copyCategories) { position ->
                adapterCategories.removeItem(position)
            }
            rvCategories.adapter = adapterCategories

            // recycler view events
            copyEvents = events.toMutableList()
            adapterEvents = PersonalPageAdapter(copyEvents) { position ->
                adapterEvents.removeItem(position)
            }
            rvEvents.adapter = adapterEvents

            // recycler view followed organizers
            copyFollowed = followed.toMutableList()
            adapterFollowed = PersonalPageAdapter(copyFollowed) { position ->
                adapterFollowed.removeItem(position)
            }
            rvFollowed.adapter = adapterFollowed

        }

        // set username of the user and email
        tvUsername.text = username
        tvEmail.text = email

        // if no events published don't show the events rv
        if (events.isEmpty()) {
            rvEvents.visibility = View.GONE
            tvEvents.visibility = View.GONE
        }

        // if no followed organizer don't show the followed rv
        if (followed.isEmpty()) {
            rvFollowed.visibility = View.GONE
            tvFollowed.visibility = View.GONE
        }

        // save the modification of
        // the personal page
        btnSave.setOnClickListener {
            // update the user changes
            mapController.updateUser(
                copyCategories.toList(),
                copyEvents.toList(),
                copyFollowed.toList()
            )
            mapController.setPersonalPage()
        }

        // logout the user, go the main page
        btnLogout.setOnClickListener {
            // todo
        }

        // go to publish an event page
        btnPublish.setOnClickListener {
            mapController.switchFragment(PublishEventFragment(mapController))
        }

        return v
    }


}