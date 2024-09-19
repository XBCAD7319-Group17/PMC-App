package com.pmcmaApp.pmcma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Login : AppCompatActivity() {

    // Variables for login button and sign-up TextView
    private lateinit var loginButton: Button
    private lateinit var btnSignUp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize the login button and btnSignUp
        loginButton = findViewById(R.id.btnLogin) // Replace with your actual login button ID
        btnSignUp = findViewById(R.id.btnSignUp) // Replace with your actual sign-up TextView ID

        // Set click listener for the login button
        loginButton.setOnClickListener {
            // Start the MainActivity when login button is clicked
            val intent = Intent(this, MainSplashy::class.java)
            startActivity(intent)
        }

        // Set click listener for the btnSignUp TextView
        btnSignUp.setOnClickListener {
            // Start the RegisterActivity when sign-up TextView is clicked
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}
