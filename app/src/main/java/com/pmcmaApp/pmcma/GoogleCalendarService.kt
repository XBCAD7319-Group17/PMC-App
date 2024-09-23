package com.pmcmaApp.pmcma

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import java.io.InputStream
import android.content.Context


class GoogleCalendarService(private val context: Context) {

    private val applicationName = "PMCMA"
    private val jsonKeyFile = "credentials.json" // Store the file in the assets folder

    fun getCalendarService(): Calendar {
        val credential: GoogleCredential = loadCredentials()

        return Calendar.Builder(
            credential.transport,
            credential.jsonFactory,
            credential
        )
            .setApplicationName(applicationName)
            .build()
    }

    private fun loadCredentials(): GoogleCredential {
        val inputStream: InputStream = context.assets.open(jsonKeyFile)
        return GoogleCredential.fromStream(inputStream)
            .createScoped(listOf(CalendarScopes.CALENDAR_READONLY))
    }

    fun getEvents(calendarId: String): List<Event> {
        val service = getCalendarService()
        val events = service.events().list(calendarId)
            .setMaxResults(10)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()

        return events.items
    }
}
