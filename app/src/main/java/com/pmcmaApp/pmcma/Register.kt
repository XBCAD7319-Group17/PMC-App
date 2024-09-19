package com.pmcmaApp.pmcma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Register : AppCompatActivity() {

    // Variables for buttons
    private lateinit var btnBackToLogin: Button
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize buttons
        btnBackToLogin = findViewById(R.id.btnBackToLogin) // Replace with your actual ID for btnBackToLogin
        btnSignUp = findViewById(R.id.btnSignUp) // Replace with your actual ID for btnSignUp

        // Set click listener for the back to login button
        btnBackToLogin.setOnClickListener {
            // Start the Login activity when the back to login button is clicked
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Optional: Finish the current activity to remove it from the back stack
        }

        // Set click listener for the sign-up button
        btnSignUp.setOnClickListener {
            // Also, start the Login activity when sign-up button is clicked
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Optional: Finish the current activity to remove it from the back stack
        }
    }
}
