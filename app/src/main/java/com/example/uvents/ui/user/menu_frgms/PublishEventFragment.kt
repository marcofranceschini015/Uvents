package com.example.uvents.ui.user.menu_frgms

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.MenuController
import com.example.uvents.controllers.adapter.CategoryAdapter
import com.example.uvents.model.CategorySource
import java.util.Calendar
import java.util.Locale

/**
 * Fragment with all the elements to create an publish an event
 */
class PublishEventFragment(private var menuController: MenuController) : Fragment() {

    // Elements in the view
    private lateinit var scrollView: ScrollView
    private lateinit var etInputName: EditText
    private lateinit var etInputDate: EditText
    private lateinit var etInputLocation: EditText
    private lateinit var recyclerViewCat: RecyclerView
    private lateinit var etInputDescription: EditText
    private lateinit var btnUploadImage: Button
    private lateinit var btnPublish: Button
    private lateinit var etInputTime: EditText
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when the Fragment is at least Started.
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                menuController.setPersonalPage()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

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
            scrollView = v.findViewById(R.id.scrollview)
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

        etInputDescription.setOnClickListener {
            it.viewTreeObserver.addOnGlobalLayoutListener {
                val rect = Rect()
                it.getWindowVisibleDisplayFrame(rect)
                val screenHeight = it.rootView.height

                // Calculate the keyboard height
                val keypadHeight = screenHeight - rect.bottom

                // Check if the keyboard is shown or hidden
                if (keypadHeight > screenHeight * 0.15) {
                    // Keyboard is shown
                    scrollView.translationY = (-keypadHeight.toFloat() * 0.22).toFloat()
                } else {
                    // Keyboard is hidden
                    scrollView.translationY = 0f
                }
            }
        }

        // get the category of the event
        val categoryList = CategorySource(menuController.menuActivity).getCategoryList()
        val adapter = CategoryAdapter(categoryList)
        recyclerViewCat.adapter = adapter


        var fileUri: Uri? = null
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                fileUri = it
            }
        }

        // Publish an event with some fun
        // to check forms validation, category selection
        // image selection and location selection
        btnPublish.setOnClickListener {
            val checkedItems = adapter.getCheckedItems()
            val checkedTextsArray = checkedItems.toList()

            if (validateForms()) {
                if (menuController.nameExists(etInputName.text.toString())) {
                    showToast("Name already taken")
                } else if (!menuController.locationExist(etInputLocation.text.toString())) {
                    showToast("Location doesn't exist")
                } else {
                    handleCategoryAndPublish(checkedTextsArray, fileUri)
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

        val timePickerDialog = TimePickerDialog(menuController.menuActivity, { _, selectedHour, selectedMinute ->
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
            menuController.menuActivity, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
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


    /**
     * Check if the forms are empty or filled
     */
    private fun validateForms(): Boolean {
        if (etInputName.text.isEmpty() || etInputDate.text.isEmpty() ||
            etInputTime.text.isEmpty() || etInputLocation.text.isEmpty() ||
            etInputDescription.text.isEmpty()) {
            showToast("Please fill all the forms")
            return false
        }
        return true
    }


    /**
     * Handle the category selected and if it's ok
     * go to publish event
     */
    private fun handleCategoryAndPublish(checkedTextsArray: List<String>, fileUri: Uri?) {
        if (checkedTextsArray.size == 1) {
            fileUri?.let {
                publishEvent(checkedTextsArray[0], it)
            } ?: showToast("Please take an image")
        } else {
            showToast("You have to select only one category")
        }
    }


    /**
     * Publish event if everything it's ok
     */
    private fun publishEvent(category: String, fileUri: Uri) {
        menuController.publishEvent(
            etInputName.text.toString(),
            etInputDate.text.toString(),
            etInputTime.text.toString(),
            etInputLocation.text.toString(),
            etInputDescription.text.toString(),
            category,
            fileUri
        )
        showToast("Published, wait...")
    }


    /**
     * Manage the showing of a toast message
     */
    private fun showToast(message: String) {
        Toast.makeText(
            menuController.menuActivity,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

}