package com.casaos.client.network

import android.util.Log
import com.casaos.client.data.*
import retrofit2.Response
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
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
        
        // Create lenient Gson instance
        val gson = GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create()) // For plain text responses
            .addConverterFactory(GsonConverterFactory.create(gson)) // For JSON responses
            .build()
            
        apiService = retrofit.create(CasaOSApiService::class.java)
        webService = retrofit.create(CasaOSWebService::class.java)
    }
    
    private fun createOkHttpClient(config: CasaOSConfig): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        
        // Add logging interceptor for debugging
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("CasaOS-Network", message)
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
        
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
            
            // Try the home page first (most likely to work)
            try {
                val homeResponse = apiService!!.getHomePage()
                if (homeResponse.isSuccessful) {
                    val body = homeResponse.body()
                    // Check if response looks like CasaOS (contains typical elements)
                    if (body != null && (body.contains("CasaOS") || body.contains("casa") || 
                        body.contains("<!DOCTYPE html") || body.contains("<html"))) {
                        return ConnectionStatus(isConnected = true, message = "Connected to CasaOS web interface")
                    }
                    return ConnectionStatus(isConnected = true, message = "Server responding")
                }
            } catch (e: Exception) {
                // Home page failed, try health endpoint
            }
            
            // Try health endpoint as fallback
            try {
                val healthResponse = apiService!!.getSystemHealth()
                if (healthResponse.isSuccessful) {
                    return ConnectionStatus(isConnected = true, message = "Health endpoint responding")
                }
            } catch (e: Exception) {
                // Health endpoint also failed
            }
            
            // Try a simple ping to see if server is reachable
            try {
                val webService = webService ?: return ConnectionStatus(
                    isConnected = false,
                    errorMessage = "Service not initialized"
                )
                
                val pingResponse = webService.getPage(config.getBaseUrl())
                if (pingResponse.isSuccessful) {
                    return ConnectionStatus(isConnected = true, message = "Server reachable")
                } else {
                    return ConnectionStatus(
                        isConnected = false,
                        errorMessage = "Server responded with HTTP ${pingResponse.code()}"
                    )
                }
            } catch (e: Exception) {
                return ConnectionStatus(
                    isConnected = false,
                    errorMessage = "Connection failed: ${e.message}"
                )
            }
            
        } catch (e: Exception) {
            ConnectionStatus(
                isConnected = false,
                errorMessage = "Connection error: ${e.message}"
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
            val response = apiService!!.login(loginRequest)
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data?.token != null) {
                    authToken = apiResponse.data.token
                    // Recreate services with auth token
                    createServices(config)
                    return ConnectionStatus(isConnected = true)
                } else {
                    return ConnectionStatus(
                        isConnected = false,
                        errorMessage = apiResponse?.message ?: "Login failed"
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
    
    // App Management Methods
    suspend fun getApps(): Response<ApiResponse<List<AppInfo>>> {
        return apiService!!.getApps()
    }
    
    suspend fun startApp(appId: String): Response<ApiResponse<Unit>> {
        return apiService!!.startApp(appId)
    }
    
    suspend fun stopApp(appId: String): Response<ApiResponse<Unit>> {
        return apiService!!.stopApp(appId)
    }
    
    suspend fun restartApp(appId: String): Response<ApiResponse<Unit>> {
        return apiService!!.restartApp(appId)
    }
    
    // System Information Methods
    suspend fun getSystemInfo(): Response<ApiResponse<SystemInfo>> {
        return apiService!!.getSystemInfo()
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