package com.example.uvents.ui.user.menu_frgms

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.uvents.R
import com.example.uvents.controllers.MenuController
import java.security.PrivateKey

/**
 * Show every single event with the
 * info of it
 */
class EventFragment(
    private val menuController: MenuController,
    private val name: String,
    private val organizerName: String,
    private val category: String,
    private val date: String,
    private val time: String,
    private val description: String,
    private val address: String,
    private val uid: String,
    private val imageUrl: String,
    private val eid: String) : Fragment() {

    // view elements
    private lateinit var nameEvent: TextView
    private lateinit var nameOrganizer: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvDescription: TextView
    private lateinit var location: TextView
    private lateinit var ivShare: ImageView
    private lateinit var ivFollow: ImageView
    private lateinit var ivUnfollow: ImageView
    private lateinit var ivChat: ImageView
    private lateinit var ivAddCategory: ImageView
    private lateinit var ivRemoveCategory: ImageView
    private lateinit var imageEvent: ImageView
    private lateinit var btnBook: Button


    /**
     * Manage the back button of android
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when the Fragment is at least Started.
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                menuController.switchFragment(MapFragment(menuController))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    /**
     * When the view is created set up everything
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View? = inflater.inflate(R.layout.fragment_event, container, false)

        if(v != null) {
            // view connection
            nameEvent = v.findViewById(R.id.nameEvent)
            nameOrganizer = v.findViewById(R.id.organizerName)
            tvCategory = v.findViewById(R.id.category)
            tvDate = v.findViewById(R.id.date)
            tvTime = v.findViewById(R.id.time)
            tvDescription = v.findViewById(R.id.description)
            location = v.findViewById(R.id.location)
            ivShare = v.findViewById(R.id.shareEvent)
            ivFollow = v.findViewById(R.id.follow)
            ivUnfollow = v.findViewById(R.id.unfollow)
            ivChat = v.findViewById(R.id.chat)
            ivAddCategory = v.findViewById(R.id.addCategory)
            ivRemoveCategory = v.findViewById(R.id.removeCategory)
            imageEvent = v.findViewById(R.id.imageEvent)
            btnBook = v.findViewById(R.id.btnBook)
        }

        // set every value of the view
        nameEvent.text = name
        nameOrganizer.text = organizerName
        tvCategory.text = category
        tvDate.text = date
        tvTime.text = time
        tvDescription.text = description
        location.text = address
        loadImage(imageUrl)

        // remove interactivity if I'm the organizer
        if(menuController.ImtheOrganizer(uid)) {
            ivFollow.visibility = View.GONE
            ivUnfollow.visibility = View.GONE
            ivChat.visibility = View.GONE
            btnBook.visibility = View.GONE

            // Show number of participants to the event
        } else {
            // Check if event already booked
            if (menuController.isBooked(eid)){
                btnBook.visibility = View.GONE
            }

            // check if already followed change the follow in unfollow
            if(menuController.isFollowed(uid)) {
                ivFollow.visibility = View.GONE
                ivUnfollow.visibility = View.VISIBLE
            } else {
                ivFollow.visibility = View.VISIBLE
                ivUnfollow.visibility = View.GONE
            }
        }

        // if the category of the event is already in the liked one
        // show the remove category button
        // otherwise the add
        if(menuController.isFavouriteCategory(category)) {
            ivAddCategory.visibility = View.GONE
        } else {
            ivRemoveCategory.visibility = View.GONE
        }


        // set the text for the sharing
        // of the event
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

        // listener for the add/remove category
        ivAddCategory.setOnClickListener {
            menuController.addCategory(category)
            ivAddCategory.visibility = View.GONE
            ivRemoveCategory.visibility = View.VISIBLE
        }

        ivRemoveCategory.setOnClickListener {
            menuController.removeCategory(category)
            ivAddCategory.visibility = View.VISIBLE
            ivRemoveCategory.visibility = View.GONE
        }

        ivChat.setOnClickListener {
            val intent = Intent(menuController.mapActivity, ChatActivity::class.java)
            intent.putExtra("name", organizerName)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }

        ivFollow.setOnClickListener {
            menuController.follow(uid, organizerName)
            ivFollow.visibility = View.GONE
            ivUnfollow.visibility = View.VISIBLE
        }

        ivUnfollow.setOnClickListener {
            menuController.removeFollow(uid)
            ivFollow.visibility = View.VISIBLE
            ivUnfollow.visibility = View.GONE
        }

        btnBook.setOnClickListener {
            menuController.bookEvent(eid, name)
            btnBook.visibility = View.GONE
        }

        return v
    }


    /**
     * Load an image from url into the imageView
     */
    private fun loadImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .into(imageEvent)
    }

}