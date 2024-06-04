package com.example.uvents.ui.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.model.Event

class EventFragment(private var event: Event) : Fragment() {

    private lateinit var nameEvent: TextView
    private lateinit var nameOrganizer: TextView
    private lateinit var category: TextView
    private lateinit var description: TextView
    private lateinit var location: TextView

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
            category = v.findViewById(R.id.category)
            description = v.findViewById(R.id.description)
            location = v.findViewById(R.id.location)
        }

        nameEvent.text = event.name
        nameOrganizer.text = event.organizer
        category.text = event.category
        description.text = event.description
        location.text = event.address

        return v
    }

}