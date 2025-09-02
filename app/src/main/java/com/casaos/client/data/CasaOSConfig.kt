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

// System Information Models
data class SystemInfo(
    val cpu: CpuInfo,
    val memory: MemoryInfo,
    val disk: DiskInfo,
    val network: NetworkInfo,
    val uptime: Long,
    val version: String
)

data class CpuInfo(
    val usage: Float,
    val temperature: Float,
    val cores: Int,
    val model: String
)

data class MemoryInfo(
    val total: Long,
    val used: Long,
    val available: Long,
    val usagePercent: Float
)

data class DiskInfo(
    val total: Long,
    val used: Long,
    val available: Long,
    val usagePercent: Float
)

data class NetworkInfo(
    val upload: Long,
    val download: Long,
    val uploadSpeed: Long,
    val downloadSpeed: Long
)

// App Management Models
data class AppInfo(
    val id: String,
    val name: String,
    val icon: String,
    val status: AppStatus,
    val version: String,
    val description: String,
    val ports: List<AppPort>,
    val volumes: List<AppVolume>,
    val cpuUsage: Float,
    val memoryUsage: Long,
    val networkUsage: NetworkUsage
)

enum class AppStatus {
    RUNNING, STOPPED, STARTING, STOPPING, ERROR
}

data class AppPort(
    val containerPort: Int,
    val hostPort: Int,
    val protocol: String
)

data class AppVolume(
    val containerPath: String,
    val hostPath: String,
    val mode: String
)

data class NetworkUsage(
    val rxBytes: Long,
    val txBytes: Long
)

// File Management Models
data class FileInfo(
    val name: String,
    val path: String,
    val size: Long,
    val isDirectory: Boolean,
    val lastModified: Long,
    val permissions: String,
    val owner: String,
    val mimeType: String?
)

data class DirectoryListing(
    val currentPath: String,
    val parentPath: String?,
    val files: List<FileInfo>,
    val totalSize: Long,
    val fileCount: Int,
    val directoryCount: Int
)

// Terminal Models
data class TerminalSession(
    val id: String,
    val isActive: Boolean,
    val lastActivity: Long
)

// API Response Models
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class SystemHealth(
    val status: String,
    val uptime: Long,
    val services: List<ServiceStatus>
)

data class ServiceStatus(
    val name: String,
    val status: String,
    val port: Int?
)