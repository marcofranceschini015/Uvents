package com.example.uvents.ui.user.menu_frgms

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.MenuController
import com.example.uvents.controllers.adapter.AdvSearchCategoryAdapter
import com.example.uvents.model.CategorySource
import java.util.Calendar
import java.util.Locale

/**
 * Fragment that contains all the utilities for the advanced research
 */
class AdvancedSearchFragment(private val menuController: MenuController) : Fragment() {

    private lateinit var ivClose: ImageView
    private lateinit var rvCategory: RecyclerView
    private lateinit var tvDateFrom: TextView
    private lateinit var tvDateTo: TextView
    private lateinit var etTimeFrom: EditText
    private lateinit var etOrganizer: EditText
    private lateinit var btnApply: Button
    private lateinit var btnReset: Button
    private lateinit var checkFollowed: CheckBox

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_advanced_search, container, false)

        if(v != null) {
            ivClose = v.findViewById(R.id.close)
            rvCategory = v.findViewById(R.id.rvCategory)
            tvDateFrom = v.findViewById(R.id.fromDate)
            tvDateTo = v.findViewById(R.id.toDate)
            etTimeFrom = v.findViewById(R.id.fromTime)
            etOrganizer = v.findViewById(R.id.organizerName)
            btnApply = v.findViewById(R.id.btnApply)
            btnReset = v.findViewById(R.id.btnReset)
            checkFollowed = v.findViewById(R.id.checkFollowed)
        }

        ivClose.setOnClickListener {
            menuController.switchFragment(MapFragment(menuController))
        }

        val categoryList = CategorySource(menuController.menuActivity).getCategoryList()
        val adapter = AdvSearchCategoryAdapter(categoryList)
        rvCategory.adapter = adapter

        tvDateFrom.setOnClickListener {
            clickDatePicker(true)
        }

        tvDateTo.setOnClickListener {
            clickDatePicker(false)
        }

        etTimeFrom.setOnClickListener {
            openTimePickerDialog(etTimeFrom)
        }

        // When apply a filtered search
        // at least do 1 research
        btnApply.setOnClickListener {
            if(!checkFollowed.isChecked && etOrganizer.text.isEmpty() && tvDateFrom.text.isEmpty() && tvDateTo.text.isEmpty() &&
            etTimeFrom.text.isEmpty() && adapter.getCheckedItems().isEmpty() ) {

                menuController.printToast("At least one filter must be applied")

            } else {
                menuController.applyFilteredSearch(checkFollowed.isChecked, etOrganizer.text.toString(), tvDateFrom.text.toString(), tvDateTo.text.toString(),
                etTimeFrom.text.toString(), adapter.getCheckedItems())
                menuController.printToast("Filter successfully applied")
                menuController.menuActivity.backHome()
            }
        }

        // When click All Events reset all the events
        btnReset.setOnClickListener {
            menuController.resetView()
            menuController.printToast("Restore all events")
            menuController.menuActivity.backHome()
        }

        return v
    }


    /**
     * Open the time picker dialog when click on the
     * edit text time
     */
    private fun openTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(menuController.menuActivity, { _, selectedHour, selectedMinute ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            editText.setText(formattedTime)  // Set the time to the EditText that was passed in
        }, hour, minute, true)

        timePickerDialog.show()
    }


    /**
     * Open the date picker and select a date
     */
    private fun clickDatePicker(startingDate: Boolean) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(menuController.menuActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            if(startingDate) {
                tvDateFrom.text = "${month + 1}/$dayOfMonth/$year"
            } else {
                tvDateTo.text = "${month + 1}/$dayOfMonth/$year"
            }

        }, year, month, day).show()

    }


}