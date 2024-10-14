package com.pmcmaApp.pmcma

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager

class NGODetailActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var ngoName: TextView
    private lateinit var ngoDescription: TextView
    private lateinit var ngoEmail: TextView
    private lateinit var ngoPhone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ngodetail)

        viewPager = findViewById(R.id.viewPager)
        ngoName = findViewById(R.id.ngo_name)
        ngoDescription = findViewById(R.id.ngo_description)
        ngoEmail = findViewById(R.id.ngo_email)
        ngoPhone = findViewById(R.id.ngo_phone)

        val ngo: NGO = intent.getParcelableExtra("ngo") ?: return
        ngoName.text = ngo.name
        ngoDescription.text = ngo.description
        ngoEmail.text = "Email: ${ngo.email}"
        ngoPhone.text = "Phone: ${ngo.phone}"

        val imagePagerAdapter = ImagePagerAdapter(this, ngo.images)
        viewPager.adapter = imagePagerAdapter
    }
}
