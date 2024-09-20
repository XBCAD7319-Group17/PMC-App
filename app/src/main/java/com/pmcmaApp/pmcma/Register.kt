package com.pmcmaApp.pmcma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class Register : AppCompatActivity() {

    // Variables for buttons
    private lateinit var phoneNumber: EditText
    private lateinit var houseAddress: EditText
    private lateinit var RegEmail: EditText
    private lateinit var Regpassword: EditText
    private lateinit var RegconfirmPassword: EditText
    private lateinit var btnBackToLogin: Button
    private lateinit var btnSignUp: Button
    lateinit var mAuth: FirebaseAuth
    // Firestore instance
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        // Typecasting
        phoneNumber = findViewById(R.id.phoneNumber)
        houseAddress = findViewById(R.id.houseAddress)
        RegEmail = findViewById(R.id.RegEmail)
        Regpassword = findViewById(R.id.Regpassword)
        RegconfirmPassword = findViewById(R.id.RegconfirmPassword)
        btnBackToLogin = findViewById(R.id.btnBackToLogin) // Replace with your actual ID for btnBackToLogin
        btnSignUp = findViewById(R.id.btnSignUp) // Replace with your actual ID for btnSignUp
        mAuth = FirebaseAuth.getInstance()


        //initializing firebase
        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set click listener for the back to login button
        btnBackToLogin.setOnClickListener {
            // Start the Login activity when the back to login button is clicked
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Optional: Finish the current activity to remove it from the back stack
        }

        // Set click listener for the sign-up button
        btnSignUp.setOnClickListener {
            SignUp()
        }


    }

    private fun SignUp() {
        val phone = phoneNumber.text.toString().trim()
        val house = houseAddress.text.toString().trim()
        val email = RegEmail.text.toString().trim()
        val pass = Regpassword.text.toString().trim()
        val confirmPassword = RegconfirmPassword.text.toString().trim()

        //validate inputs

        if (phone.isBlank() || house.isBlank() || email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
        }

        //validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
        }

        //validate password match
        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm password do not match", Toast.LENGTH_SHORT)
                .show()
        }


        //creating the user in firebase
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    //successfully created the user
                    val user = mAuth.currentUser
                    val userId = user?.uid

                    //prepare user data for Firestore
                    val userData = hashMapOf(
                        "email" to email,
                        "phone" to phone,
                        "houseAddress" to house
                    )

                    // Save additional data in Firestore
                    if (userId != null) {
                        db.collection("users").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "You have successfully signed up", Toast.LENGTH_SHORT).show()

                                // Navigate to the login page
                                val intent = Intent(this@Register, Login::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save additional data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Handle specific errors during sign-up
                    handleSignUpError(task.exception)
                }
            }
    }


    private fun handleSignUpError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                // Weak password
                Toast.makeText(
                    this,
                    "Weak password. Password should be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is FirebaseAuthInvalidCredentialsException -> {
                // Invalid email format
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            }

            is FirebaseAuthUserCollisionException -> {
                // Email already registered
                Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT).show()
            }

            else -> {
                // General error
                Toast.makeText(
                    this,
                    "Sign Up failed: ${exception?.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
