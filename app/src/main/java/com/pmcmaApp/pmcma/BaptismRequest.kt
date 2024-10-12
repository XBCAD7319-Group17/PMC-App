package com.pmcmaApp.pmcma

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

class BaptismRequest : Fragment() {

    private lateinit var bptsmName: TextInputEditText
    private lateinit var bptsmEmail: TextInputEditText
    private lateinit var bptsmPhone: TextInputEditText
    private lateinit var bptsmReq: TextInputEditText
    private lateinit var bptsmHouse: TextInputEditText
    private lateinit var btn_submit: Button
    private lateinit var btn_back_home: Button
    private lateinit var requestTypeSpinner: Spinner
    private var selectedRequestType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_baptism_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bptsmName = view.findViewById(R.id.bptsmName)
        bptsmEmail = view.findViewById(R.id.bptsmEmail)
        bptsmPhone = view.findViewById(R.id.bptsmPhone)
        bptsmReq = view.findViewById(R.id.bptsmReq)
        bptsmHouse = view.findViewById(R.id.bptsmHouse)
        btn_submit = view.findViewById(R.id.btn_submit)
        btn_back_home = view.findViewById(R.id.btn_back_home)
        requestTypeSpinner = view.findViewById(R.id.requestTypeSpinner)

        btn_submit.setOnClickListener {
            submitBaptismRequest()
        }

        btn_back_home.setOnClickListener {
            activity?.onBackPressed()
        }

        setupSpinner()
    }

    private fun setupSpinner() {
        // Load the string array from resources
        val requestTypes = resources.getStringArray(R.array.request_type_array)

        // Create an adapter and set it to the spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            requestTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        requestTypeSpinner.adapter = adapter

        // Handle item selection
        requestTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedRequestType = requestTypes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedRequestType = ""
            }
        }
    }

    private fun submitBaptismRequest() {
        val name = bptsmName.text.toString().trim()
        val email = bptsmEmail.text.toString().trim()
        val phone = bptsmPhone.text.toString().trim()
        val specialRequests = bptsmReq.text.toString().trim()
        val address = bptsmHouse.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
            specialRequests.isEmpty() || address.isEmpty()) {
            Toast.makeText(activity, "Please fill in all the fields before you submit", Toast.LENGTH_SHORT).show()
            return
        }

        val emailBody = """
            Dear [Recipient's Name or Church Name],

            You have received a new request of type: $selectedRequestType.

            Details:
            - Name: $bptsmName
            - Email Address: $bptsmEmail
            - Phone Number: $bptsmPhone
            - House Address: $bptsmHouse
            - Special Requests or Comments: $bptsmReq

            Please reach out to the individual for further details.

            Best regards,
            [Your Church's Name or App Name]
        """.trimIndent()

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:recipient@example.com") // Replace with actual recipient email
            putExtra(Intent.EXTRA_SUBJECT, "Request: $selectedRequestType from $name")
            putExtra(Intent.EXTRA_TEXT, emailBody)
        }

        if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(emailIntent)
        } else {
            Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show()
        }
    }
}
