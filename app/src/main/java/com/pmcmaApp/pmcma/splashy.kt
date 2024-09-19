package com.pmcmaApp.pmcma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.Timer
import java.util.TimerTask

class splashy : AppCompatActivity() {
    //variable
    lateinit var imageView : ImageView
    val delay : Long = 12000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashy)
            //typecasting
            imageView = findViewById(R.id.imageViewload)

            //animated image code
            Glide.with(this).load(R.drawable.pmcgif).into(imageView)
            //create a timer object
            val runSplash = Timer()
            //task to do after
            val showSplash = object : TimerTask()
            {
                override fun run() {
                    //will close the main
                    finish()
                    //move to the next screen
                    val intentOne = Intent(this@splashy, Login::class.java)
                    startActivity(intentOne)
                }//run method ends
            }//object ends
            runSplash.schedule(showSplash,delay)

        }
    }

