package com.casaos.client.network

import com.casaos.client.data.*
import retrofit2.Response
import retrofit2.http.*

interface CasaOSApiService {
    
    // Authentication
    @POST("/v1/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ApiResponse<LoginResponse>>
    
    @POST("/v1/auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>
    
    // System Information
    @GET("/v1/sys/health")
    suspend fun getSystemHealth(): Response<ApiResponse<SystemHealth>>
    
    @GET("/v1/sys/hardware")
    suspend fun getSystemInfo(): Response<ApiResponse<SystemInfo>>
    
    @GET("/v1/sys/version")
    suspend fun getSystemVersion(): Response<ApiResponse<String>>
    
    @GET("/v1/sys/version/current")
    suspend fun getCurrentVersion(): Response<String>
    
    // App Management (v2 API)
    @GET("/v2/app_management/apps")
    suspend fun getApps(): Response<ApiResponse<List<AppInfo>>>
    
    @GET("/v2/app_management/apps/{id}")
    suspend fun getApp(@Path("id") appId: String): Response<ApiResponse<AppInfo>>
    
    @POST("/v2/app_management/apps/{id}/start")
    suspend fun startApp(@Path("id") appId: String): Response<ApiResponse<Unit>>
    
    @POST("/v2/app_management/apps/{id}/stop")
    suspend fun stopApp(@Path("id") appId: String): Response<ApiResponse<Unit>>
    
    @POST("/v2/app_management/apps/{id}/restart")
    suspend fun restartApp(@Path("id") appId: String): Response<ApiResponse<Unit>>
    
    @DELETE("/v2/app_management/apps/{id}")
    suspend fun removeApp(@Path("id") appId: String): Response<ApiResponse<Unit>>
    
    @GET("/v2/app_management/apps/{id}/logs")
    suspend fun getAppLogs(@Path("id") appId: String, @Query("lines") lines: Int = 100): Response<ApiResponse<String>>
    
    // File Management (v3 API)
    @GET("/v3/file/list")
    suspend fun listFiles(@Query("path") path: String): Response<ApiResponse<DirectoryListing>>
    
    @GET("/v3/file/info")
    suspend fun getFileInfo(@Query("path") path: String): Response<ApiResponse<FileInfo>>
    
    @POST("/v3/file/create")
    suspend fun createDirectory(@Query("path") path: String): Response<ApiResponse<Unit>>
    
    @DELETE("/v3/file/delete")
    suspend fun deleteFile(@Query("path") path: String): Response<ApiResponse<Unit>>
    
    @POST("/v3/file/rename")
    suspend fun renameFile(@Query("old_path") oldPath: String, @Query("new_path") newPath: String): Response<ApiResponse<Unit>>
    
    @POST("/v3/file/copy")
    suspend fun copyFile(@Query("src") srcPath: String, @Query("dst") dstPath: String): Response<ApiResponse<Unit>>
    
    @POST("/v3/file/move")
    suspend fun moveFile(@Query("src") srcPath: String, @Query("dst") dstPath: String): Response<ApiResponse<Unit>>
    
    // Terminal/SSH
    @GET("/v1/sys/wsssh")
    suspend fun getTerminalSession(): Response<ApiResponse<TerminalSession>>
    
    @POST("/v1/sys/ssh-login")
    suspend fun sshLogin(@Body loginRequest: LoginRequest): Response<ApiResponse<Unit>>
    
    // Basic connectivity
    @GET("/")
    suspend fun getHomePage(): Response<String>
    
    @GET("/ping")
    suspend fun ping(): Response<String>
}

interface CasaOSWebService {
    @GET
    suspend fun getPage(@Url url: String): Response<String>
}