package com.pmcmaApp.pmcma

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import android.content.Context
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Calendar

class GoogleCalendarService(private val context: Context) {

    private val applicationName = "PMCMA"
    private val base64Credentials = "ewogICJ0eXBlIjogInNlcnZpY2VfYWNjb3VudCIsCiAgInByb2plY3RfaWQiOiAicG1jbWEtNWJkMmMiLAogICJwcml2YXRlX2tleV9pZCI6ICIwZWUwNjM0YTFhZTJlMDRlOTAyYzVmYzVlM2EwMjQzN2NhZDliYjNmIiwKICAicHJpdmF0ZV9rZXkiOiAiLS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tXG5NSUlFdmdJQkFEQU5CZ2txaGtpRzl3MEJBUUVGQUFTQ0JLZ3dnZ1NrQWdFQUFvSUJBUUN2WVFwRjh3MW1JMkhpXG5mbkI4OGhGZFdvTjY3UjRVNmxaN1l0Y0ZBN3p6MHpXeXpxeE84bE5aNlBHOW9FUlVKSGY0cGhvbkFxbjBRRng2XG5sMkVZV2F3QmNMRUJKbU5zR2lxbnByQ1pjUndYM2YrNWg4Y3V6WUxmQkJHaG51TUZSZ0tQaGtwMkVSMXhEaEtoXG5iRzBBN09KNURGd0hDTHlCeHE5bzRMNzZpcC8wUjVYMTFZZWxKSUo0ajhOTXRTTmw3TXdpczJSTHNTanJmbkhRXG4reHBCaTZ4L1N0d250N1h1SHcrbDVkWjJtU3ZWR1MwLzYyZ2g3K08rK0s1NlJOT25VOHpZTGJBRUFpbWlBQm12XG5nc0thQ1VYVUtOeXVmOE9yMWhDQ2hzanNTMUN5K3ZJc0ErV0Qxb2JnVjJWeHBUTm9ZVlBBQUp2VTZNQTdva0t6XG5XcFBjcVRUMUFnTUJBQUVDZ2dFQUdRR1VwZW1rZVBKRmxEWmtFcVN2VGlqSVE0Qkl3RUd6aUE5TlNsVkFuV0Y1XG5zQitnMlBiYlpKLyt6QU5zaXJUU2VGMHlzR3BFVW1QVTY5UG16OEtGY0lVaGIrRUozeEdTRUdxSGxyRldpSjZxXG5Sa2x5WUoyRDhnbVIvQUg4SzdYU1NxV2l1MWZ3WXp6VzhqWlVDb1E4UEhkRExldVVlVXFibXpBa2V2UEdIOTNTXG51Y04rWWk2OGlFRDMyRnFSWFZrUDZGL1ZiRWVHdDFadEVWUER2QUtkZVJZNXlQVHNNdjhHbXlCSEQxSXo0UEZOXG5PU0pjUTZmcm1UaFoxS2VBNklFQTE5ZDhVN04xNDY0TkQxQm1wNWoxZFQ0RHM1TU1YMkMvY0VNS09LN1I5WE1yXG55bVhITG15cnNKQlF1dVZqMXh6SkhWbnpsQlNRdzJOUFpLSEtsdElwQndLQmdRRFdWUGExZzFPdlU5Z3ZuZXdTXG5ZQiszdk9HRWVRU0F2dUI1UGRQelRzVkZjOVBFNEw4UTFaK3IrTHM0RUYzWDV4cE1jbi9UTWlnQWh0aHd0cTRFXG5FaUhjNEx2djlyUHJUVEI1TnRhQ0ovNmdjTGcxcWdlVUI1TEJQYzVBNVF0M3VVa0hneURWV21CNkgvKzJ1MDRGXG43ZE1iVWlTS1pKWklWckZ1bGlqdVlBWDRGd0tCZ1FEUmVYRWNQeUE5UXVTLzlaQ1pDQWt0MWlMT21nSDBNQW1qXG5tWTN3Z1YvT1JFQlJDVWl3N3RLTDV5cWtHOUZNOTVmSWdRMWJVcVd0VE91MVhEY3lwVkhtRkxpdjNxRUg5ZUxoXG5LQk5VU3pSMjFtVEFINDZjYXk0MHlrYWlRUEdkYjlsNE5qOVVlMFRmQ1BmM20wK3hMeGNqYlMvSEkvZFRxaVc0XG4zdG1KRm9wVzB3S0JnRDhSejJ0SVV0YlQ4RmpLdXM0SmRTdm5LK0dFanZ1bnIzeGQ0a0hGbXkwOTBVSXM5R3hxXG53RXFscmNub253VEtYNlhCUjdZSkcxWlVWOXFMRnhmaGtnOWlIa3VWcUFvRXM4L25nQ1hheVFYRFJhR2RBQS9pXG5UcUFJcE9uWnZJOGxlY1Q1SkM5RUlnR3Q2dHlGcjRiMUhkcjdSLzlsYkFlcll4anNuT3pRV3RGUEFvR0JBSVdqXG50OER3bVEzY1pubjlIYkkwQ1Q4ZjlYYmZsRTJJZEMzV29sS0ZCMjFkNTdhTGVML0FyYnlwejIrQmU4ZGgwb2tOXG5wRXJWOEhNZXR1WXVuZHlHMGpnNmtoVkpzR3lXdDVjejdSa0RIY2FQUXhtN1NFMk1wNTd3U1ozc1Z2azlTWDNEXG5CaVpNdW9TY2dTNkwvSXlxNFZVdHJLU2Mwb2FpNzR3QlNNR0lDdHdYQW9HQkFNTFhVV1VVckE3V0x1QjJrN1U1XG5TSTZpa2xsb3FPYTBjQkxxRjM4VTd5VXdxdmZCVUlhVERpYVNNbnk4dXlQN0dtY2V2NmdBck51UEExUk9odWpwXG5qeFh4cFVoR1ZwRitZUHJwdTdhSmJQZm8rSjB2NHFwb3ZJWDFVOVhOUG9BdUN6cTlWOTNnUXNxSWQzVGF2UEozXG5UeUhHc0lVU0RXVlEyd3dOWUFwbEYveDhcbi0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS1cbiIsCiAgImNsaWVudF9lbWFpbCI6ICJjYWxlbmRhckBwbWNtYS01YmQyYy5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIsCiAgImNsaWVudF9pZCI6ICIxMDk0ODg0OTUxMzUzNjA2NDI0MzQiLAogICJhdXRoX3VyaSI6ICJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20vby9vYXV0aDIvYXV0aCIsCiAgInRva2VuX3VyaSI6ICJodHRwczovL29hdXRoMi5nb29nbGVhcGlzLmNvbS90b2tlbiIsCiAgImF1dGhfcHJvdmlkZXJfeDUwOV9jZXJ0X3VybCI6ICJodHRwczovL3d3dy5nb29nbGVhcGlzLmNvbS9vYXV0aDIvdjEvY2VydHMiLAogICJjbGllbnRfeDUwOV9jZXJ0X3VybCI6ICJodHRwczovL3d3dy5nb29nbGVhcGlzLmNvbS9yb2JvdC92MS9tZXRhZGF0YS94NTA5L2NhbGVuZGFyJTQwcG1jbWEtNWJkMmMuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLAogICJ1bml2ZXJzZV9kb21haW4iOiAiZ29vZ2xlYXBpcy5jb20iCn0K"

    fun getCalendarService(): Calendar {
        val credential: GoogleCredential = loadCredentialsFromBase64()

        return Calendar.Builder(
            credential.transport,
            credential.jsonFactory,
            credential
        )
            .setApplicationName(applicationName)
            .build()
    }

    private fun loadCredentialsFromBase64(): GoogleCredential {
        // Decode the base64 string
        val decodedBytes = Base64.decode(base64Credentials, Base64.DEFAULT)
        val inputStream: InputStream = ByteArrayInputStream(decodedBytes)

        // Create GoogleCredential from the input stream
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
