package com.example.uvents.ui.user.menu_frgms

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.MapController

class EventFragment(
    private val mapController: MapController,
    private val name: String,
    private val organizerName: String,
    private val category: String,
    private val date: String,
    private val description: String,
    private val address: String) : Fragment() {

    private lateinit var nameEvent: TextView
    private lateinit var nameOrganizer: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvDescription: TextView
    private lateinit var location: TextView
    private lateinit var ivShare: ImageView
    private lateinit var ivFollow: ImageView
    private lateinit var ivChat: ImageView
    private lateinit var ivAddCategory: ImageView
    private lateinit var ivRemoveCategory: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when the Fragment is at least Started.
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                mapController.switchFragment(MapFragment(mapController))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_event, container, false)

        if(v != null) {
            nameEvent = v.findViewById(R.id.nameEvent)
            nameOrganizer = v.findViewById(R.id.organizerName)
            tvCategory = v.findViewById(R.id.category)
            tvDate = v.findViewById(R.id.date)
            tvDescription = v.findViewById(R.id.description)
            location = v.findViewById(R.id.location)
            ivShare = v.findViewById(R.id.shareEvent)
            ivFollow = v.findViewById(R.id.follow)
            ivChat = v.findViewById(R.id.chat)
            ivAddCategory = v.findViewById(R.id.addCategory)
            ivRemoveCategory = v.findViewById(R.id.removeCategory)
        }

        nameEvent.text = name
        nameOrganizer.text = organizerName
        tvCategory.text = category
        tvDate.text = date
        tvDescription.text = description
        location.text = address

        if(mapController.isFavouriteCategory(category)) {
            ivAddCategory.visibility = View.GONE
        } else {
            ivRemoveCategory.visibility = View.GONE
        }

        ivShare.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                val sharedText = "Name: " + name + "\n" +
                                 "Organizer: " + organizerName + "\n" +
                                 "Category: " + category + "\n" +
                                 "Description: " + description + "\n" +
                                 "Location: " + address
                putExtra(Intent.EXTRA_TEXT, sharedText)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share via")
            startActivity(shareIntent)
        }

        ivAddCategory.setOnClickListener {
            mapController.addCategory(category)
            ivAddCategory.visibility = View.GONE
            ivRemoveCategory.visibility = View.VISIBLE
        }

        ivRemoveCategory.setOnClickListener {
            mapController.removeCategory(category)
            ivAddCategory.visibility = View.VISIBLE
            ivRemoveCategory.visibility = View.GONE
        }

        return v
    }

}