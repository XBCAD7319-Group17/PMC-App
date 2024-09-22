package com.pmcmaApp.pmcma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    // Variables for UI components
    private lateinit var edtHouseAddress: EditText
    private lateinit var edtPhoneNumber: EditText
    private lateinit var edtEmail: EditText
    private lateinit var btnSveChange: Button
    private lateinit var btnBack: Button

    // Firebase Authentication and Database references
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbReference: DocumentReference
    private lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        dbReference = FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)

        // Bind UI components to code
        edtHouseAddress = view.findViewById(R.id.edtHouseAddress)
        edtPhoneNumber = view.findViewById(R.id.edtPhoneNumber)
        edtEmail = view.findViewById(R.id.edtEmail)
        btnSveChange = view.findViewById(R.id.btnSveChange)
        btnBack = view.findViewById(R.id.btnBack)

        // Fetch user profile data from Firebase and update UI
        fetchUserProfile { houseAddress, phoneNumber, email ->
            edtHouseAddress.setText(houseAddress)
            edtPhoneNumber.setText(phoneNumber)
            edtEmail.setText(email)
        }

        // Set click listeners for buttons
        btnSveChange.setOnClickListener {
            saveProfileChanges()
        }

        btnBack.setOnClickListener {
            // Handle back navigation
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun fetchUserProfile(callback: (String, String, String) -> Unit) {
        dbReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Get user details from the database
                val houseAddress = documentSnapshot.getString("houseAddress") ?: ""
                val phoneNumber = documentSnapshot.getString("phoneNumber") ?: ""
                val email = currentUser.email ?: "" // Fetch email directly from FirebaseAuth

                // Pass the data to the callback
                callback(houseAddress, phoneNumber, email)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileChanges() {
        val newHouseAddress = edtHouseAddress.text.toString().trim()
        val newPhoneNumber = edtPhoneNumber.text.toString().trim()
        val newEmail = edtEmail.text.toString().trim()

        // Update email in FirebaseAuth if changed
        if (newEmail != currentUser.email) {
            currentUser.updateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Email updated successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update email.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Create a map for updated user info (houseAddress, phoneNumber, email)
        val updatedUserInfo = hashMapOf(
            "houseAddress" to newHouseAddress,
            "phoneNumber" to newPhoneNumber,
            "email" to newEmail
        )

        // Update Firestore with new house address and phone number
        dbReference.update(updatedUserInfo as Map<String, Any>).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Profile updated successfully.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
