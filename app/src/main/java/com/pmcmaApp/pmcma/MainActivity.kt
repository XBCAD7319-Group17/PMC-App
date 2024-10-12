package com.pmcmaApp.pmcma

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout // side nav
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var navHeaderView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupToolbar()
        setupDrawer()
        setupBottomNavigation()

        // Load the default fragment (Home)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set user details in the navigation header
        setUserDetailsInNavHeader()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navHeaderView = navigationView.getHeaderView(0)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupDrawer() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            handleBottomNavigation(menuItem)
        }
    }

    private fun handleBottomNavigation(menuItem: MenuItem): Boolean {
        val fragment: Fragment = when (menuItem.itemId) {
            R.id.bottom_home -> HomeFragment()
            R.id.bottom_notices -> {
                Toast.makeText(this, "getString(R.string.write_notes)", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.bottom_bible -> BibleFragment()
            R.id.bottom_events -> EventsFragment()
            R.id.bottom_profile -> ProfileFragment()
            else -> HomeFragment()
        }
        loadFragment(fragment)
        return true
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setUserDetailsInNavHeader() {
        val user = auth.currentUser
        val userNameTextView = navHeaderView.findViewById<TextView>(R.id.userName)
        val userEmailTextView = navHeaderView.findViewById<TextView>(R.id.userEmail)

        if (user != null) {
            // Set user email and display name (or default name)
            userNameTextView.text = user.displayName ?: "Display Name not available"
            userEmailTextView.text = user.email ?: "Email not available"
        } else {
            userNameTextView.text = "User"
            userEmailTextView.text = "Email not available"
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment: Fragment = when (item.itemId) {
            R.id.nav_home -> HomeFragment()
            R.id.nav_bible -> BibleFragment()
            R.id.nav_about -> AboutFragment()
            R.id.nav_events -> EventsFragment()
            R.id.nav_profile -> ProfileFragment()
            R.id.nav_donation -> DonationFragment()
            R.id.nav_requests -> BaptismRequest()
            R.id.nav_logout -> {
                // Handle logout logic
                auth.signOut()
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, Login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Clear stack
                startActivity(intent)
                finish()
                return true
            }


            else -> HomeFragment()
        }
        loadFragment(fragment)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


}
