package com.example.uvents.ui.user.menu_frgms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.MenuController
import com.example.uvents.controllers.adapter.BookedEventsAdapter

class BookFragment(
    private val menuController: MenuController,
    private val events: Map<String, String>) : Fragment() {

    private lateinit var copyEvents: MutableMap<String, String>
    private lateinit var bookedEventsAdapter: BookedEventsAdapter
    private lateinit var eventsRecyclerView: RecyclerView

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_book, container, false)

        if (v != null) {
            eventsRecyclerView = v.findViewById(R.id.rvEvents)
        }

        copyEvents = events.toMutableMap()
        bookedEventsAdapter = BookedEventsAdapter(copyEvents) {key->
            bookedEventsAdapter.removeItem(key)
            menuController.removeBook(key)
            menuController.printToast("Book removed")
        }
        eventsRecyclerView.adapter = bookedEventsAdapter

        return v
    }


}