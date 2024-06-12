package com.example.uvents.ui.user.menu_frgms

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.example.uvents.controllers.adapter.AdvSearchCategoryAdapter
import com.example.uvents.model.CategorySource
import com.example.uvents.model.Event
import java.util.Calendar

class AdvancedSearchFragment(private val mapController: MapController) : Fragment() {

    private lateinit var ivClose: ImageView
    private lateinit var rvCategory: RecyclerView
    private lateinit var tvDateFrom: TextView
    private lateinit var tvDateTo: TextView
    private lateinit var etTimeFrom: EditText
    private lateinit var etTimeTo: EditText
    private lateinit var etOrganizer: EditText
    private lateinit var btnApply: Button

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
            tvDateFrom = v.findViewById(R.id.fromDate)
            tvDateTo = v.findViewById(R.id.toDate)
            etTimeFrom = v.findViewById(R.id.fromTime)
            etTimeTo = v.findViewById(R.id.toTime)
            etOrganizer = v.findViewById(R.id.organizerName)
            btnApply = v.findViewById(R.id.btnApply)
        }

        ivClose.setOnClickListener {
            mapController.switchFragment(MapFragment(mapController))
        }

        val categoryList = CategorySource(mapController.mapActivity).getCategoryList()
        val adapter = AdvSearchCategoryAdapter(categoryList)
        rvCategory.adapter = adapter

        tvDateFrom.setOnClickListener {
            clickDatePicker(true)
        }

        tvDateTo.setOnClickListener {
            clickDatePicker(false)
        }

        val events = getDummyEvents()

        btnApply.setOnClickListener {
//            val debugOrganizer = etOrganizer.text.isNotEmpty()
            if(etOrganizer.text.isEmpty() && tvDateFrom.text.isEmpty() && tvDateTo.text.isEmpty() &&
            etTimeFrom.text.isEmpty() && etTimeTo.text.isEmpty() && adapter.getCheckedItems().isEmpty() ) {

                mapController.printToast("At least one filter must be applied")

            } else {

                val filteredEvents = mapController.applyFilteredSearch(events, etOrganizer.text, tvDateFrom.text, tvDateTo.text,
                etTimeFrom.text, etTimeTo.text, adapter.getCheckedItems())

                mapController.printToast("Filter successfully applied")
                mapController.switchFragment(MapFragment(mapController)) // todo filter in controller logic
            }
        }

        return v
    }

    private fun clickDatePicker(startingDate: Boolean) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(mapController.mapActivity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            if(startingDate) {
                tvDateFrom.text = "$dayOfMonth/${month + 1}/$year"
            } else {
                tvDateTo.text = "$dayOfMonth/${month + 1}/$year"
            }

//            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)
//
//            val theDate = sdf.parse(selectedDate)
//            val selectedDateInDays = theDate.time / 86_400_000
//
//            val currentDate = sdf.parse(sdf.format(System.currentTimeMillis()))
//            val currentDateInDays = currentDate.time / 86_400_000
//
//            val diffDates = currentDateInDays - selectedDateInDays
//
//            tvResults?.text = "${diffDates.toString()} days have passed"

        }, year, month, day).show()

    }

    // Dummy data for events, consider retrieving this from a ViewModel or similar component
    private fun getDummyEvents(): ArrayList<Event> {
        return arrayListOf(
            Event(
                "Festa universitaria",
                "Unibs",
                "Party",
                "20/06/2024",
                "Festa di Primavera per staccare dallo stress dello studio e degli esami",
                "Via Branze, 38, Brescia"),
            Event(
                "Mostra di Picasso",
                "Belle Arti Brescia",
                "Charity",
                "28/06/2024",
                "Vengono esposti i quadri più particolari dell'autore",
                "Piazza della Vittoria, Brescia"
            ),
            Event(
                "Eras Tour Taylor Swift",
                "San Siro Concerts",
                "Concert",
                "14/07/2024",
                "Concerto di 3 ore con 40 canzoni dell'artista. Occasione unica per vedere Taylor Swift in Italia.",
                "Stadio San Siro, Milano"
            )
        )
    }


}