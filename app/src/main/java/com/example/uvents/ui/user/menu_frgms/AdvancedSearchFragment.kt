package com.example.uvents.ui.user.menu_frgms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.example.uvents.controllers.adapter.AdvSearchCategoryAdapter
import com.example.uvents.model.CategorySource

class AdvancedSearchFragment(private val mapController: MapController) : Fragment() {

    private lateinit var ivClose: ImageView
    private lateinit var rvCategory: RecyclerView

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_advanced_search, container, false)

        if(v != null) {
            ivClose = v.findViewById(R.id.close)
            rvCategory = v.findViewById(R.id.rvCategory)
        }

        ivClose.setOnClickListener {
            mapController.switchFragment(MapFragment(mapController))
        }

        val categoryList = CategorySource(mapController.mapActivity).getCategoryList()
        val adapter = AdvSearchCategoryAdapter(categoryList)
        rvCategory.adapter = adapter

        return v
    }


}