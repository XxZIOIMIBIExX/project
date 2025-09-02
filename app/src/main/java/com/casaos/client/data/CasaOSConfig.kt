package com.casaos.client.data

data class CasaOSConfig(
    val serverUrl: String = "",
    val port: Int = 80,
    val useHttps: Boolean = false,
    val username: String = "",
    val password: String = ""
) {
    fun getBaseUrl(): String {
        val protocol = if (useHttps) "https" else "http"
        val portSuffix = if ((useHttps && port == 443) || (!useHttps && port == 80)) "" else ":$port"
        return "$protocol://$serverUrl$portSuffix"
    }
    
    fun isValid(): Boolean {
        return serverUrl.isNotBlank() && port > 0 && port <= 65535
    }
}

data class ConnectionStatus(
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String? = null,
    val message: String? = null
)