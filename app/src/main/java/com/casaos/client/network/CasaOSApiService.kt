package com.casaos.client.network

import com.casaos.client.data.LoginRequest
import com.casaos.client.data.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface CasaOSApiService {
    
    @POST("/v1/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @GET("/v1/sys/health")
    suspend fun checkHealth(): Response<Map<String, Any>>
    
    @GET("/")
    suspend fun getHomePage(): Response<String>
}

interface CasaOSWebService {
    @GET
    suspend fun getPage(@Url url: String): Response<String>
}