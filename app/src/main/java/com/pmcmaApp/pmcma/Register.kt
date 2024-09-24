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
import com.google.firebase.auth.UserProfileChangeRequest
import java.lang.Exception

class Register : AppCompatActivity() {

    // Variables for buttons
    private lateinit var fullName: EditText
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
        fullName = findViewById(R.id.fullName)
        phoneNumber = findViewById(R.id.phoneNumber)
        houseAddress = findViewById(R.id.houseAddress)
        RegEmail = findViewById(R.id.RegEmail)
        Regpassword = findViewById(R.id.Regpassword)
        RegconfirmPassword = findViewById(R.id.RegconfirmPassword)
        btnBackToLogin = findViewById(R.id.btnBackToLogin)
        btnSignUp = findViewById(R.id.btnSignUp)
        mAuth = FirebaseAuth.getInstance()

        // Initializing Firebase
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()

        // Set click listener for the back to login button
        btnBackToLogin.setOnClickListener {
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
        val name = fullName.text.toString().trim()
        val phone = phoneNumber.text.toString().trim()
        val house = houseAddress.text.toString().trim()
        val email = RegEmail.text.toString().trim()
        val pass = Regpassword.text.toString().trim()
        val confirmPassword = RegconfirmPassword.text.toString().trim()

        // Validate inputs
        if (name.isBlank() || phone.isBlank() || house.isBlank() || email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate password match
        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Creating the user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Successfully created the user
                    val userId = mAuth.currentUser?.uid
                    val userInfo = hashMapOf(
                        "fullname" to name,         // Save fullname string
                        "email" to email,           // Save email string
                        "phone" to phone,           // Save phone string
                        "houseAddress" to house     // Save house address string
                    )

                    // Save additional data in Firestore
                    if (userId != null) {
                        db.collection("users").document(userId).set(userInfo)
                            .addOnSuccessListener {
                                // Update user profile with display name
                                mAuth.currentUser?.updateProfile(
                                    UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()
                                )?.addOnCompleteListener { profileUpdateTask ->
                                    if (profileUpdateTask.isSuccessful) {
                                        Toast.makeText(this, "User details have been saved", Toast.LENGTH_SHORT).show()
                                        // Navigate to login page after successful signup
                                        val intent = Intent(this@Register, Login::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Failed to update display name", Toast.LENGTH_SHORT).show()
                                    }
                                }
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

    // Handling different sign-up error cases
    private fun handleSignUpError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                Toast.makeText(
                    this,
                    "Weak password. Password should be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            }

            is FirebaseAuthUserCollisionException -> {
                Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT).show()
            }

            else -> {
                Toast.makeText(
                    this,
                    "Sign Up failed: ${exception?.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
