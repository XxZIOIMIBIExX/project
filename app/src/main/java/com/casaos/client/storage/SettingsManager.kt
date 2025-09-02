package com.casaos.client.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.casaos.client.data.CasaOSConfig

class SettingsManager(private val context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = try {
        EncryptedSharedPreferences.create(
            context,
            "settings",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback to regular SharedPreferences if encryption fails
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    
    fun saveConfig(config: CasaOSConfig) {
        sharedPreferences.edit().apply {
            putString(KEY_SERVER_URL, config.serverUrl)
            putInt(KEY_SERVER_PORT, config.port)
            putBoolean(KEY_USE_HTTPS, config.useHttps)
            putString(KEY_USERNAME, config.username)
            putString(KEY_PASSWORD, config.password)
            apply()
        }
    }
    
    fun loadConfig(): CasaOSConfig {
        return CasaOSConfig(
            serverUrl = sharedPreferences.getString(KEY_SERVER_URL, "") ?: "",
            port = sharedPreferences.getInt(KEY_SERVER_PORT, 80),
            useHttps = sharedPreferences.getBoolean(KEY_USE_HTTPS, false),
            username = sharedPreferences.getString(KEY_USERNAME, "") ?: "",
            password = sharedPreferences.getString(KEY_PASSWORD, "") ?: ""
        )
    }
    
    fun clearConfig() {
        sharedPreferences.edit().clear().apply()
    }
    
    fun hasConfig(): Boolean {
        return sharedPreferences.contains(KEY_SERVER_URL) && 
               sharedPreferences.getString(KEY_SERVER_URL, "")?.isNotBlank() == true
    }
    
    fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false) && hasConfig()
    }
    
    fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }
    
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }
    
    fun clearAuthToken() {
        sharedPreferences.edit().remove(KEY_AUTH_TOKEN).apply()
    }
    
    companion object {
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_SERVER_PORT = "server_port"
        private const val KEY_USE_HTTPS = "use_https"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_AUTH_TOKEN = "auth_token"
        
        @Volatile
        private var INSTANCE: SettingsManager? = null
        
        fun getInstance(context: Context): SettingsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}