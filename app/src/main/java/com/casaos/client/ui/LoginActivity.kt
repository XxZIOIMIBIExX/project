package com.casaos.client.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.casaos.client.R
import com.casaos.client.data.CasaOSConfig
import com.casaos.client.data.LoginRequest
import com.casaos.client.network.NetworkManager
import com.casaos.client.storage.SettingsManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var serverUrlInput: TextInputEditText
    private lateinit var portInput: TextInputEditText
    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator
    
    private lateinit var serverUrlLayout: TextInputLayout
    private lateinit var portLayout: TextInputLayout
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    
    private val networkManager = NetworkManager.getInstance()
    private lateinit var settingsManager: SettingsManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        settingsManager = SettingsManager.getInstance(this)
        
        initViews()
        loadSavedSettings()
        setupClickListeners()
        
        // Check if already logged in
        if (settingsManager.isLoggedIn()) {
            navigateToMain()
        }
    }
    
    private fun initViews() {
        serverUrlLayout = findViewById(R.id.serverUrlLayout)
        portLayout = findViewById(R.id.portLayout)
        usernameLayout = findViewById(R.id.usernameLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        
        serverUrlInput = findViewById(R.id.serverUrlInput)
        portInput = findViewById(R.id.portInput)
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        progressIndicator = findViewById(R.id.progressIndicator)
    }
    
    private fun loadSavedSettings() {
        val config = settingsManager.loadConfig()
        serverUrlInput.setText(config.serverUrl)
        portInput.setText(config.port.toString())
        usernameInput.setText(config.username)
        // Don't pre-fill password for security
    }
    
    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            attemptLogin()
        }
    }
    
    private fun attemptLogin() {
        clearErrors()
        
        val serverUrl = serverUrlInput.text.toString().trim()
        val portText = portInput.text.toString().trim()
        val username = usernameInput.text.toString().trim()
        val password = passwordInput.text.toString()
        
        // Validation
        var hasError = false
        
        if (serverUrl.isEmpty()) {
            serverUrlLayout.error = "Server URL is required"
            hasError = true
        }
        
        if (portText.isEmpty()) {
            portLayout.error = "Port is required"
            hasError = true
        }
        
        val port = try {
            portText.toInt()
        } catch (e: NumberFormatException) {
            portLayout.error = "Invalid port number"
            hasError = true
            0
        }
        
        if (port <= 0 || port > 65535) {
            portLayout.error = "Port must be between 1 and 65535"
            hasError = true
        }
        
        if (hasError) return
        
        val config = CasaOSConfig(
            serverUrl = serverUrl,
            port = port,
            useHttps = port == 443, // Auto-detect HTTPS
            username = username,
            password = password
        )
        
        performLogin(config)
    }
    
    private fun performLogin(config: CasaOSConfig) {
        setLoading(true)
        
        lifecycleScope.launch {
            try {
                val connectionStatus = if (config.username.isNotEmpty() && config.password.isNotEmpty()) {
                    networkManager.login(config)
                } else {
                    networkManager.testConnection(config)
                }
                
                if (connectionStatus.isConnected) {
                    // Save configuration
                    settingsManager.saveConfig(config)
                    settingsManager.setLoggedIn(true)
                    
                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    showError(connectionStatus.errorMessage ?: "Login failed")
                }
            } catch (e: Exception) {
                showError("Connection error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }
    
    private fun setLoading(loading: Boolean) {
        loginButton.isEnabled = !loading
        progressIndicator.visibility = if (loading) View.VISIBLE else View.GONE
        
        // Disable input fields during loading
        serverUrlInput.isEnabled = !loading
        portInput.isEnabled = !loading
        usernameInput.isEnabled = !loading
        passwordInput.isEnabled = !loading
    }
    
    private fun clearErrors() {
        serverUrlLayout.error = null
        portLayout.error = null
        usernameLayout.error = null
        passwordLayout.error = null
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}