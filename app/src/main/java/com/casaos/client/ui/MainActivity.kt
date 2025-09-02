package com.casaos.client.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.casaos.client.R
import com.casaos.client.storage.SettingsManager
import com.casaos.client.ui.fragments.AppsFragment
import com.casaos.client.ui.fragments.FilesFragment
import com.casaos.client.ui.fragments.SettingsFragment
import com.casaos.client.ui.fragments.StatusFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNavigation: BottomNavigationView
    
    private lateinit var settingsManager: SettingsManager
    
    // Fragment instances
    private val statusFragment = StatusFragment()
    private val appsFragment = AppsFragment()
    private val filesFragment = FilesFragment()
    private val settingsFragment = SettingsFragment()
    
    private var activeFragment: Fragment = statusFragment
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        settingsManager = SettingsManager.getInstance(this)
        
        // Check if user is logged in
        if (!settingsManager.isLoggedIn()) {
            navigateToLogin()
            return
        }
        
        setContentView(R.layout.activity_main_new)
        
        initViews()
        setupBottomNavigation()
        setupFragments()
    }
    
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        
        setSupportActionBar(toolbar)
        supportActionBar?.title = "CasaOS"
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_status -> {
                    showFragment(statusFragment)
                    toolbar.title = "System Status"
                    true
                }
                R.id.nav_apps -> {
                    showFragment(appsFragment)
                    toolbar.title = "Applications"
                    true
                }
                R.id.nav_files -> {
                    showFragment(filesFragment)
                    toolbar.title = "File Manager"
                    true
                }
                R.id.nav_settings -> {
                    showFragment(settingsFragment)
                    toolbar.title = "Settings"
                    true
                }
                else -> false
            }
        }
        
        // Set default selection
        bottomNavigation.selectedItemId = R.id.nav_status
    }
    
    private fun setupFragments() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, statusFragment, "STATUS")
            add(R.id.fragmentContainer, appsFragment, "APPS")
            add(R.id.fragmentContainer, filesFragment, "FILES")
            add(R.id.fragmentContainer, settingsFragment, "SETTINGS")
            
            hide(appsFragment)
            hide(filesFragment)
            hide(settingsFragment)
            
            commit()
        }
    }
    
    private fun showFragment(fragment: Fragment) {
        if (fragment == activeFragment) return
        
        supportFragmentManager.beginTransaction().apply {
            hide(activeFragment)
            show(fragment)
            commit()
        }
        
        activeFragment = fragment
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu_new, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_refresh -> {
                refreshCurrentFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun logout() {
        settingsManager.setLoggedIn(false)
        navigateToLogin()
    }
    
    private fun refreshCurrentFragment() {
        when (activeFragment) {
            is StatusFragment -> statusFragment.refresh()
            is AppsFragment -> appsFragment.refresh()
            is FilesFragment -> filesFragment.refresh()
            is SettingsFragment -> settingsFragment.refresh()
        }
    }
}