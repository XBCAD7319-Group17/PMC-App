package com.pmcmaApp.pmcma

import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.ListView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.api.services.calendar.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventsFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var eventsListView: ListView
    private lateinit var progressBar: ProgressBar
    private val events: MutableMap<String, MutableList<EventDetails>> = mutableMapOf()
    private val calendarId = "pmcmassociation@gmail.com"
    private var selectedDate: String? = null

    data class EventDetails(val summary: String, val startTime: String, val endTime: String, val startDateTimeMillis: Long, val endDateTimeMillis: Long)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendar)
        eventsListView = view.findViewById(R.id.eventsListView)
        progressBar = view.findViewById(R.id.progressBar)

        fetchEvents()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
            displayEventsForDate(selectedDate!!)
        }

        eventsListView.setOnItemClickListener { _, _, position, _ ->
            selectedDate?.let {
                val selectedEvent = events[it]?.get(position)
                selectedEvent?.let { eventDetails ->
                    showConfirmationDialog(eventDetails)
                }
            }
        }
    }

    private fun fetchEvents() {
        progressBar.visibility = View.VISIBLE
        calendarView.isEnabled = false

        AsyncTask.execute {
            val googleCalendarService = GoogleCalendarService(requireContext())
            val fetchedEvents: List<Event> = googleCalendarService.getEvents(calendarId)

            activity?.runOnUiThread {
                for (event in fetchedEvents) {
                    val date: String = event.start.date?.toString() ?: event.start.dateTime?.toString() ?: "No date available"
                    val formattedDate = formatDate(date)
                    val eventSummary = event.summary ?: "No title"

                    val startMillis = event.start.dateTime?.value ?: 0
                    val endMillis = event.end.dateTime?.value ?: 0

                    val startTime = formatDateTime(startMillis)
                    val endTime = formatDateTime(endMillis)

                    if (events[formattedDate] == null) {
                        events[formattedDate] = mutableListOf()
                    }
                    events[formattedDate]?.add(EventDetails(eventSummary, startTime, endTime, startMillis, endMillis))
                }

                progressBar.visibility = View.GONE
                calendarView.isEnabled = true
                displayEventsForDate(selectedDate ?: "")
            }
        }
    }

    private fun displayEventsForDate(selectedDate: String) {
        val eventList = events[selectedDate]?.map {
            "${it.summary}\nStart: ${it.startTime}\nEnd: ${it.endTime}"
        } ?: listOf("No events on this date")

        // Set the adapter to the ListView
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, eventList)
        eventsListView.adapter = adapter
    }

    private fun formatDate(dateString: String): String {
        return try {
            val dateFormat = when {
                dateString.contains("T") -> SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                else -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            }
            val date = dateFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "Invalid date: $dateString"
        }
    }

    private fun formatDateTime(millis: Long): String {
        return try {
            val dateTime = Date(millis)
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            outputFormat.format(dateTime)
        } catch (e: Exception) {
            "Invalid time"
        }
    }

    private fun showConfirmationDialog(event: EventDetails) {
        AlertDialog.Builder(requireContext())
            .setTitle("Add Event to Calendar")
            .setMessage("Do you want to add '${event.summary}' to your personal calendar?")
            .setPositiveButton("Yes") { _, _ ->
                addEventToLocalCalendar(event)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun addEventToLocalCalendar(event: EventDetails) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = "vnd.android.cursor.item/event"
            putExtra("beginTime", event.startDateTimeMillis)
            putExtra("endTime", event.endDateTimeMillis)
            putExtra("title", event.summary)
            putExtra("description", "Added from the app")
        }
        startActivity(intent)
    }
}
