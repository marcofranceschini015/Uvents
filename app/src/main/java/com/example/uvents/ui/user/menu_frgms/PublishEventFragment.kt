package com.example.uvents.ui.user.menu_frgms

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.example.uvents.controllers.adapter.CategoryAdapter
import com.example.uvents.model.CategorySource
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Locale

/**
 * Fragment with all the elements to create an publish an event
 */
class PublishEventFragment(private var mapController: MapController) : Fragment() {

    // Elements in the view
    private lateinit var etInputName: EditText
    private lateinit var etInputDate: EditText
    private lateinit var etInputLocation: EditText
    private lateinit var recyclerViewCat: RecyclerView
    private lateinit var etInputDescription: EditText
    private lateinit var btnUploadImage: Button
    private lateinit var btnPublish: Button
    private lateinit var etInputTime: EditText
    private lateinit var checkedTextsArray: List<String>

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>


    /**
     * When the view is created set up everything
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_publish_event, container, false)

        if (v != null) {
            // link with the view
            etInputName = v.findViewById(R.id.etInputName)
            etInputDate = v.findViewById(R.id.etInputDate)
            etInputLocation = v.findViewById(R.id.etInputLocation)
            recyclerViewCat = v.findViewById(R.id.recyclerViewCat)
            etInputDescription = v.findViewById(R.id.etInputDescription)
            btnUploadImage = v.findViewById(R.id.btnUploadImage)
            btnPublish = v.findViewById(R.id.btnPublish)
            etInputTime = v.findViewById(R.id.etInputTime)
        }

        // set up date and time
        etInputDate.setOnClickListener {
            openDatePickerDialog(etInputDate)
        }

        etInputTime.setOnClickListener {
            openTimePickerDialog(etInputTime)
        }

        // get the category of the event
        val categoryList = CategorySource(mapController.mapActivity).getCategoryList()
        val adapter = CategoryAdapter(categoryList)
        recyclerViewCat.adapter = adapter


        var fileUri: Uri? = null
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                fileUri = it
            }
        }

        // when click publish check empty things
        // and that the category selected is exactly one
        btnPublish.setOnClickListener {
            val checkedItems = adapter.getCheckedItems()
            checkedTextsArray = checkedItems.toList()

            // Check empty forms
            if (etInputName.text.isEmpty() ||
                etInputDate.text.isEmpty() ||
                etInputTime.text.isEmpty() ||
                etInputLocation.text.isEmpty() ||
                etInputDescription.text.isEmpty()
            ) {
                Toast.makeText(
                    mapController.mapActivity,
                    "Please fill all the forms",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // check one category
                if (checkedTextsArray.size == 1) {
                    if (fileUri != null) {
                        val category = checkedTextsArray[0]
                        mapController.publishEvent(
                            etInputName.text.toString(),
                            etInputDate.text.toString(),
                            etInputTime.text.toString(),
                            etInputLocation.text.toString(),
                            etInputDescription.text.toString(),
                            category,
                            fileUri!!
                        )
                        Toast.makeText(
                            mapController.mapActivity,
                            "Event successfully published",
                            Toast.LENGTH_SHORT
                        ).show()
                        // modify the view of the profile after publication
                        mapController.setPersonalPage()
                    }
                    else {
                        Toast.makeText(
                            mapController.mapActivity,
                            "Please take an image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        mapController.mapActivity,
                        "You have to select only one category",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnUploadImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
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

        val timePickerDialog = TimePickerDialog(mapController.mapActivity, { _, selectedHour, selectedMinute ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            editText.setText(formattedTime)  // Set the time to the EditText that was passed in
        }, hour, minute, true)

        timePickerDialog.show()
    }


    /**
     * Open the date picker dialog
     * when click on the date edit text
     */
    private fun openDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog to pick the date
        val datePickerDialog = DatePickerDialog(
            mapController.mapActivity, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Format the date selected
                val selectedDate = "${selectedMonth + 1}/$selectedDayOfMonth/$selectedYear"
                editText.setText(selectedDate)  // Set date in EditText
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show() // Show DatePickerDialog
    }

}