package com.pmcmaApp.pmcma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Response // Ensure this import is included
import org.json.JSONObject


class DonationFragment : Fragment() {

    private lateinit var donationCategorySpinner: Spinner
    private lateinit var otherDonationEditText: EditText
    private lateinit var checkboxMonetary: CheckBox
    private lateinit var checkboxNonMonetary: CheckBox
    private lateinit var amountEditText: EditText
    private lateinit var nonMonetaryDescriptionEditText: EditText
    private lateinit var donationDatePicker: DatePicker
    private lateinit var btnSubmitDonation: Button
    private lateinit var btnDonateWithZapper: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_donation, container, false)

        // Initialize views
        donationCategorySpinner = view.findViewById(R.id.donationCategorySpinner)
        otherDonationEditText = view.findViewById(R.id.otherDonationEditText)
        checkboxMonetary = view.findViewById(R.id.checkboxMonetaryDonation)
        checkboxNonMonetary = view.findViewById(R.id.checkboxNonMonetaryDonation)
        amountEditText = view.findViewById(R.id.donationAmountEditText)
        nonMonetaryDescriptionEditText = view.findViewById(R.id.nonMonetaryDescriptionEditText)
        donationDatePicker = view.findViewById(R.id.donationDatePicker)
        btnSubmitDonation = view.findViewById(R.id.btnSubmitDonation)
        btnDonateWithZapper = view.findViewById(R.id.btnDonateWithZapper)

        // Set up spinner with donation categories
        val donationCategories = resources.getStringArray(R.array.donation_categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, donationCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        donationCategorySpinner.adapter = adapter

        // Set up spinner listener
        donationCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (parent.getItemAtPosition(position) == "Other") {
                    otherDonationEditText.visibility = View.VISIBLE
                } else {
                    otherDonationEditText.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally handle case where nothing is selected
            }
        }

        // Checkbox listeners
        checkboxMonetary.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amountEditText.visibility = View.VISIBLE
                btnDonateWithZapper.visibility = View.VISIBLE
                btnSubmitDonation.visibility = View.GONE
            } else {
                amountEditText.visibility = View.GONE
            }
        }

        checkboxNonMonetary.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                nonMonetaryDescriptionEditText.visibility = View.VISIBLE
                donationDatePicker.visibility = View.VISIBLE
                btnSubmitDonation.visibility = View.VISIBLE
                btnDonateWithZapper.visibility = View.GONE
            } else {
                nonMonetaryDescriptionEditText.visibility = View.GONE
                donationDatePicker.visibility = View.GONE
            }
        }

        // Submit Donation button
        btnSubmitDonation.setOnClickListener {
            handleNonMonetaryDonation()
        }

        // Donate with Zapper button
        btnDonateWithZapper.setOnClickListener {
            handleMonetaryDonation()
        }

        return view
    }

    private fun handleMonetaryDonation() {
        val amount = amountEditText.text.toString()
        if (amount.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show()
            return
        }

        val category = donationCategorySpinner.selectedItem.toString()
        openZapperDonation(category, amount)
    }

    private fun handleNonMonetaryDonation() {
        val description = nonMonetaryDescriptionEditText.text.toString()
        val selectedDate = getSelectedDate()

        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show()
            return
        }

        // Handle submission logic (e.g., send email notification)
        sendEmailNotification(description, selectedDate)
        Toast.makeText(requireContext(), "Donation submitted successfully!", Toast.LENGTH_SHORT).show()
    }

    // Open Zapper with the specified category and amount
    private fun openZapperDonation(donationAmount: String, donationCategory: String) {
        val merchantId = "40648"
        val siteId = "49604"
        val apiKey = "8df789b18cbf40e2aff0c2ab847d1d4e"

        val requestBody = JSONObject().apply {
            put("amount", donationAmount)
            put("currency", "ZAR")
            put("description", "Donation for $donationCategory")
        }

        val url = "https://api.zapper.com/business/api/v1/merchants/$merchantId/sites/$siteId/payments"

        val request = object : JsonObjectRequest(Method.POST, url, requestBody,
            Response.Listener { response ->
                Toast.makeText(requireContext(), "Payment successful: ${response.toString()}", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error: VolleyError ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                    "Accept" to "application/json",
                    "x-api-key" to apiKey
                )
            }
        }

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    // Get selected date from the date picker
    private fun getSelectedDate(): String {
        val day = donationDatePicker.dayOfMonth
        val month = donationDatePicker.month + 1 // Months are indexed from 0
        val year = donationDatePicker.year
        return "$day/$month/$year"
    }

    // Send email notification to admin
    private fun sendEmailNotification(description: String, date: String) {
        val recipientEmail = "pmcmassociation@gmail.com" // Admin's email
        val subject = "New Donation Notification"
        val messageBody = "Donation Description: $description\nDonation Date: $date"

        // Execute the email sending task
        EmailSender(recipientEmail, subject, messageBody).execute()
        Toast.makeText(requireContext(), "Email notification sent for donation: $description on $date", Toast.LENGTH_SHORT).show()
    }
}
