package com.example.uvents.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


/**
 * Class used to manage the advanced search feature for an event
 */
class EventSearcher(private val events: List<Event>) {


    /**
     * Filtered search giving all the information from
     * the view
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun filteredSearch(
        organizerName: String?,
        fromDate: String?,
        toDate: String?,
        fromTime: String?,
        checkedCategories: List<String>): List<Event> {

        val filteredEvents: MutableList<Event> = mutableListOf()

        // Get which check I have to do, based on empty or null statement
        val checkOrganizer = !organizerName.isNullOrEmpty()
        val checkInitDate = !fromDate.isNullOrEmpty()
        val checkEndDate = !toDate.isNullOrEmpty()
        val checkInitTime = !fromTime.isNullOrEmpty()
        val checkCategory = checkedCategories.isNotEmpty()

        // for each events in the list filter
        events.forEach{e ->
            if (checkOrganizer(e, organizerName, checkOrganizer) &&
                checkDate(e, checkInitDate, checkEndDate, fromDate, toDate) &&
                checkTime(e, checkInitTime, fromTime) &&
                checkCategory(e, checkCategory, checkedCategories))
                filteredEvents.add(e)
        }

        return filteredEvents.toList()
    }


    /**
     * Check if the name of the organizer insert in the view
     * match the event considered. Return true if we don't have
     * to check the organizer
     */
    private fun checkOrganizer(event: Event, organizerName: String?, checkOrganizer: Boolean): Boolean {
        val eventOrganizerName = event.organizerName

        return when {
            checkOrganizer && eventOrganizerName != organizerName -> false
            else -> true
        }
    }


    /**
     * Check if the date insert in the view
     * match the event considered. Return true if we don't have
     * to check the date
     */
    private fun checkDate(event: Event, checkInit: Boolean, checkEnd: Boolean, fromDate: String?, toDate: String?): Boolean {
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.US)

        val eventDate = format.parse(event.date!!)
        var from : Date? = null
        var to : Date? = null
        if (!fromDate.isNullOrEmpty())
            from = fromDate.let { format.parse(it) }
        if (!toDate.isNullOrEmpty())
            to = toDate.let { format.parse(it) }

        return when {
            checkInit && eventDate!! < from -> false
            checkEnd && eventDate!! > to -> false
            else -> true
        }
    }


    /**
     * Check if the time insert in the view
     * match the event considered. Return true if we don't have
     * to check the time
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkTime(event: Event, checkTime: Boolean, time: String?): Boolean{
        val eventTime = parseTime(event.time!!)
        var init : LocalTime? = null
        if (!time.isNullOrEmpty())
            init = parseTime(time)

        return when {
            checkTime && eventTime < init -> false
            else -> true
        }
    }


    /**
     * Parse the time from a string input
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseTime(timeString: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(timeString, formatter)
    }


    /**
     * Check if the categories insert in the view
     * match the event considered. Return true if we don't have
     * to check the category
     */
    private fun checkCategory(event: Event, checkCategory: Boolean, checkedCategories: List<String>): Boolean{
        return when{
            checkCategory && !checkedCategories.contains(event.category) -> false
            else -> true
        }
    }
}