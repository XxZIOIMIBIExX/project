package com.casaos.client.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.casaos.client.R
import com.casaos.client.data.CasaOSConfig
import com.casaos.client.network.NetworkManager
import com.casaos.client.utils.SettingsManager
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {
    
    private lateinit var settingsManager: SettingsManager
    private lateinit var networkManager: NetworkManager
    
    private lateinit var serverUrlPref: EditTextPreference
    private lateinit var serverPortPref: EditTextPreference
    private lateinit var useHttpsPref: SwitchPreferenceCompat
    private lateinit var usernamePref: EditTextPreference
    private lateinit var passwordPref: EditTextPreference
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        
        settingsManager = SettingsManager.getInstance(requireContext())
        networkManager = NetworkManager.getInstance()
        
        initializePreferences()
        loadCurrentConfig()
        setupPreferenceListeners()
    }
    
    private fun initializePreferences() {
        serverUrlPref = findPreference("server_url")!!
        serverPortPref = findPreference("server_port")!!
        useHttpsPref = findPreference("use_https")!!
        usernamePref = findPreference("username")!!
        passwordPref = findPreference("password")!!
    }
    
    private fun loadCurrentConfig() {
        val config = settingsManager.loadConfig()
        
        serverUrlPref.text = config.serverUrl
        serverPortPref.text = config.port.toString()
        useHttpsPref.isChecked = config.useHttps
        usernamePref.text = config.username
        passwordPref.text = config.password
        
        updateSummaries(config)
    }
    
    private fun setupPreferenceListeners() {
        serverUrlPref.setOnPreferenceChangeListener { _, newValue ->
            val url = newValue as String
            if (url.isBlank()) {
                showToast(getString(R.string.error_invalid_url))
                false
            } else {
                serverUrlPref.summary = url
                saveCurrentConfig()
                true
            }
        }
        
        serverPortPref.setOnPreferenceChangeListener { _, newValue ->
            val portStr = newValue as String
            try {
                val port = portStr.toInt()
                if (port <= 0 || port > 65535) {
                    showToast(getString(R.string.error_invalid_port))
                    false
                } else {
                    serverPortPref.summary = portStr
                    saveCurrentConfig()
                    true
                }
            } catch (e: NumberFormatException) {
                showToast(getString(R.string.error_invalid_port))
                false
            }
        }
        
        useHttpsPref.setOnPreferenceChangeListener { _, _ ->
            saveCurrentConfig()
            true
        }
        
        usernamePref.setOnPreferenceChangeListener { _, newValue ->
            val username = newValue as String
            usernamePref.summary = if (username.isBlank()) {
                getString(R.string.username_summary)
            } else {
                username
            }
            saveCurrentConfig()
            true
        }
        
        passwordPref.setOnPreferenceChangeListener { _, newValue ->
            val password = newValue as String
            passwordPref.summary = if (password.isBlank()) {
                getString(R.string.password_summary)
            } else {
                "••••••••"
            }
            saveCurrentConfig()
            true
        }
        
        // Add test connection preference
        val testConnectionPref = Preference(requireContext()).apply {
            key = "test_connection"
            title = getString(R.string.test_connection)
            summary = getString(R.string.testing_connection)
            setOnPreferenceClickListener {
                testConnection()
                true
            }
        }
        
        preferenceScreen.addPreference(testConnectionPref)
    }
    
    private fun saveCurrentConfig() {
        val config = getCurrentConfig()
        settingsManager.saveConfig(config)
    }
    
    private fun getCurrentConfig(): CasaOSConfig {
        val portStr = serverPortPref.text ?: "80"
        val port = try {
            portStr.toInt()
        } catch (e: NumberFormatException) {
            80
        }
        
        return CasaOSConfig(
            serverUrl = serverUrlPref.text ?: "",
            port = port,
            useHttps = useHttpsPref.isChecked,
            username = usernamePref.text ?: "",
            password = passwordPref.text ?: ""
        )
    }
    
    private fun updateSummaries(config: CasaOSConfig) {
        serverUrlPref.summary = config.serverUrl.ifBlank { 
            getString(R.string.server_url_summary) 
        }
        serverPortPref.summary = config.port.toString()
        usernamePref.summary = config.username.ifBlank { 
            getString(R.string.username_summary) 
        }
        passwordPref.summary = if (config.password.isBlank()) {
            getString(R.string.password_summary)
        } else {
            "••••••••"
        }
    }
    
    private fun testConnection() {
        val config = getCurrentConfig()
        
        if (!config.isValid()) {
            showToast(getString(R.string.error_invalid_url))
            return
        }
        
        val progressDialog = ProgressDialog(requireContext()).apply {
            setMessage(getString(R.string.testing_connection))
            setCancelable(false)
            show()
        }
        
        lifecycleScope.launch {
            try {
                val status = networkManager.testConnection(config)
                progressDialog.dismiss()
                
                if (status.isConnected) {
                    showToast(getString(R.string.connection_test_success))
                } else {
                    val errorMessage = status.errorMessage ?: getString(R.string.error_unknown)
                    showToast(getString(R.string.connection_test_failed, errorMessage))
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                showToast(getString(R.string.connection_test_failed, e.message ?: getString(R.string.error_unknown)))
            }
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}