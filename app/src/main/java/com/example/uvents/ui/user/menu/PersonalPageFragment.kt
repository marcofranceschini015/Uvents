package com.example.uvents.ui.user.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.MapController


class PersonalPageFragment(private val mapController: MapController,
                           private val username: String,
                           private val email:String,
                           private val categories: List<String>,
                           private val events: List<String>) : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var rvCategories: RecyclerView
    private lateinit var rvEvents: RecyclerView
    private lateinit var rvFollowed: RecyclerView
    private lateinit var btnPublish: Button
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_personal_page, container, false)

        if (v != null) {
            tvUsername = v.findViewById(R.id.tvUsername)
            tvEmail = v.findViewById(R.id.tv_email)
            rvCategories = v.findViewById(R.id.rv_categories)
            rvEvents = v.findViewById(R.id.rv_events)
            rvFollowed = v.findViewById(R.id.rv_followed)
            btnPublish = v.findViewById(R.id.btnPublish)
            btnSave = v.findViewById(R.id.btnSave)
            btnLogout = v.findViewById(R.id.btnLogout)
        }

        tvUsername.text = username
        tvEmail.text = email

        // todo set le recycler view
        // todo clicklistener dei bottoni

        return v
    }


}