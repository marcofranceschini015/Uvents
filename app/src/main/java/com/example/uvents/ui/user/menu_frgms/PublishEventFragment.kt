package com.example.uvents.ui.user.menu_frgms

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.example.uvents.controllers.adapter.CategoryAdapter
import com.example.uvents.model.CategorySource
import java.util.Calendar

class PublishEventFragment(private var mapController: MapController) : Fragment() {

    private lateinit var etInputName: EditText
    private lateinit var etInputDate: EditText
    private lateinit var etInputLocation: EditText
    private lateinit var recyclerViewCat: RecyclerView
    private lateinit var etInputDescription: EditText
    private lateinit var btnUploadImage: Button
    private lateinit var btnPublish: Button

    private lateinit var checkedTextsArray: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_publish_event, container, false)

        if (v != null) {
            etInputName = v.findViewById(R.id.etInputName)
            etInputDate = v.findViewById(R.id.etInputDate)
            etInputLocation = v.findViewById(R.id.etInputLocation)
            recyclerViewCat = v.findViewById(R.id.recyclerViewCat)
            etInputDescription = v.findViewById(R.id.etInputDescription)
            btnUploadImage = v.findViewById(R.id.btnUploadImage)
            btnPublish = v.findViewById(R.id.btnPublish)
        }

        // set up the date when click on input date
        etInputDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // DatePickerDialog to pick the date
            val datePickerDialog = DatePickerDialog(mapController.mapActivity, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Format the date selected
                val selectedDate = "${selectedMonth + 1}/$selectedDayOfMonth/$selectedYear"
                etInputDate.setText(selectedDate)  // Set date in EditText
            }, year, month, day)

            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show() // Show DatePickerDialog
        }


        // get the category of the event
        val categoryList = CategorySource(mapController.mapActivity).getCategoryList()
        val adapter = CategoryAdapter(categoryList)
        recyclerViewCat.adapter = adapter


        btnPublish.setOnClickListener {
            val checkedItems = adapter.getCheckedItems()
            checkedTextsArray = checkedItems.toList()

            // CHECK A LOT OF THINGS
            if(etInputName.text.isEmpty() ||
                etInputDate.text.isEmpty() ||
                etInputLocation.text.isEmpty() ||
                etInputDescription.text.isEmpty()){
                Toast.makeText(mapController.mapActivity, "Please fill all the forms", Toast.LENGTH_SHORT).show()
            } else {
                if(checkedTextsArray.size == 1) {
                    val category = checkedTextsArray[0]
                    mapController.publishEvent(
                        etInputName.text.toString(),
                        etInputDate.text.toString(),
                        etInputLocation.text.toString(),
                        etInputDescription.text.toString(),
                        category
                    )
                    Toast.makeText(mapController.mapActivity, "Event successfully published", Toast.LENGTH_SHORT).show()
                    mapController.setPersonalPage()
                } else {
                    Toast.makeText(mapController.mapActivity, "You have to select only one category", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return v
    }

}