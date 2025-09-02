package com.casaos.client.network

import com.casaos.client.data.CasaOSConfig
import com.casaos.client.data.ConnectionStatus
import com.casaos.client.data.LoginRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class NetworkManager {
    
    private var currentConfig: CasaOSConfig? = null
    private var apiService: CasaOSApiService? = null
    private var webService: CasaOSWebService? = null
    private var authToken: String? = null
    
    fun updateConfig(config: CasaOSConfig) {
        if (currentConfig != config) {
            currentConfig = config
            apiService = null
            webService = null
            authToken = null
            
            if (config.isValid()) {
                createServices(config)
            }
        }
    }
    
    private fun createServices(config: CasaOSConfig) {
        val okHttpClient = createOkHttpClient(config)
        val baseUrl = config.getBaseUrl()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        apiService = retrofit.create(CasaOSApiService::class.java)
        webService = retrofit.create(CasaOSWebService::class.java)
    }
    
    private fun createOkHttpClient(config: CasaOSConfig): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        
        // Add auth token if available
        authToken?.let { token ->
            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
        }
        
        // For HTTPS with self-signed certificates (development only)
        if (config.useHttps) {
            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })
                
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                
                builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        return builder.build()
    }
    
    suspend fun testConnection(config: CasaOSConfig): ConnectionStatus {
        return try {
            updateConfig(config)
            
            val service = apiService ?: return ConnectionStatus(
                isConnected = false,
                errorMessage = "Invalid configuration"
            )
            
            // Try to check health endpoint first
            val healthResponse = service.checkHealth()
            if (healthResponse.isSuccessful) {
                return ConnectionStatus(isConnected = true)
            }
            
            // If health endpoint fails, try the home page
            val homeResponse = service.getHomePage()
            if (homeResponse.isSuccessful) {
                return ConnectionStatus(isConnected = true)
            }
            
            ConnectionStatus(
                isConnected = false,
                errorMessage = "Server responded with error: ${healthResponse.code()}"
            )
            
        } catch (e: Exception) {
            ConnectionStatus(
                isConnected = false,
                errorMessage = e.message ?: "Unknown error"
            )
        }
    }
    
    suspend fun login(config: CasaOSConfig): ConnectionStatus {
        return try {
            updateConfig(config)
            
            val service = apiService ?: return ConnectionStatus(
                isConnected = false,
                errorMessage = "Invalid configuration"
            )
            
            if (config.username.isBlank() || config.password.isBlank()) {
                // Try without authentication first
                return testConnection(config)
            }
            
            val loginRequest = LoginRequest(config.username, config.password)
            val response = service.login(loginRequest)
            
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse?.success == true) {
                    authToken = loginResponse.token
                    // Recreate services with auth token
                    createServices(config)
                    return ConnectionStatus(isConnected = true)
                } else {
                    return ConnectionStatus(
                        isConnected = false,
                        errorMessage = loginResponse?.message ?: "Login failed"
                    )
                }
            } else {
                // If login endpoint doesn't exist, try without authentication
                return testConnection(config)
            }
            
        } catch (e: Exception) {
            // If login fails, try without authentication
            testConnection(config)
        }
    }
    
    fun getWebUrl(): String? {
        return currentConfig?.getBaseUrl()
    }
    
    fun isConfigured(): Boolean {
        return currentConfig?.isValid() == true
    }
    
    companion object {
        @Volatile
        private var INSTANCE: NetworkManager? = null
        
        fun getInstance(): NetworkManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkManager().also { INSTANCE = it }
            }
        }
    }
}