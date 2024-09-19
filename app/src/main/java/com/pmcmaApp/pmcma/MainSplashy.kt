package com.pmcmaApp.pmcma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView

class MainSplashy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_splashy)

        // Delaying the transition for 5 seconds (5000 milliseconds)
        Handler(Looper.getMainLooper()).postDelayed({
            // Start the About activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Finish the current activity so that the user cannot come back to this screen
            finish()
        }, 5000) // 5000 milliseconds = 5 seconds
    }
}
