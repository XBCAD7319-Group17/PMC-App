package com.pmcmaApp.pmcma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    // Variables for login button and sign-up TextView
    private lateinit var logEmail: EditText
    private lateinit var logPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var btnSignUp: TextView
    private lateinit var forgotPassword: TextView
    private lateinit var authentication: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        authentication = FirebaseAuth.getInstance()

        // Check if user is already logged in
        val currentUser = authentication.currentUser
        if (currentUser != null) {
            // User is signed in, go to MainActivity
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        // Typecasting views
        logEmail = findViewById(R.id.logEmail)
        logPassword = findViewById(R.id.logPassword)
        loginButton = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)
        forgotPassword = findViewById(R.id.forgotPassword)

        // Set click listener for the login button
        loginButton.setOnClickListener {
            val email = logEmail.text.toString().trim()
            val pass = logPassword.text.toString().trim()

            // Input validation
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email or password cannot be empty!", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, pass)
            }
        }

        // Set click listener for the sign-up TextView
        btnSignUp.setOnClickListener {
            // Start the RegisterActivity when sign-up TextView is clicked
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }

        // Set click listener for forgotPassword
        forgotPassword.setOnClickListener {
            val email = logEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            resetPassword(email)
        }
    }

    private fun loginUser(email: String, password: String) {
        // Firebase login using email and password
        authentication.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "You are now Logged in", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Display the specific reason for authentication failure
                    Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resetPassword(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error sending password reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
