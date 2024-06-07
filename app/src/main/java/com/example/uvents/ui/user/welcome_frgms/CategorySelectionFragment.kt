package com.example.uvents.ui.user.welcome_frgms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.adapter.CategoryAdapter
import com.example.uvents.controllers.WelcomeController
import com.example.uvents.model.CategorySource

/**
 * Fragment in which is possible to select from a set of categories
 */
class CategorySelectionFragment(private val ctrl: WelcomeController) : Fragment() {

    private lateinit var signBtn: Button
    private lateinit var checkedTextsArray: List<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_category_selection, container, false)
        if (v != null){
            // fill recycler view and hide arrow
            val categoryList = CategorySource(ctrl.welcomeActivity).getCategoryList()
            val recyclerView: RecyclerView = v.findViewById(R.id.recyclerViewCat)
            val adapter = CategoryAdapter(categoryList)
            recyclerView.adapter = adapter
            ctrl.hideBackArrow()

            // when click the sign button catch all the selected categories
            signBtn = v.findViewById(R.id.btnSignUp)
            signBtn.setOnClickListener {
                val checkedItems = adapter.getCheckedItems()
                // Do something with checkedItems, like creating an array
                checkedTextsArray = checkedItems.toList() // Convert List to Array if needed
                ctrl.initializeUser(checkedTextsArray)
            }
        }

        return v
    }

}