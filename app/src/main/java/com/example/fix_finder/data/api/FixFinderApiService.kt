package com.example.fix_finder.data.api

import com.example.fix_finder.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface FixFinderApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>

    @GET("api/categories")
    suspend fun getCategories(): Response<List<ServiceCategory>>

    @GET("api/providers")
    suspend fun getProviders(
        @Query("query") query: String? = null,
        @Query("category") category: String? = null,
        @Query("location") location: String? = null
    ): Response<List<ServiceProvider>>

    @GET("api/providers/{id}")
    suspend fun getProviderById(
        @Path("id") id: String
    ): Response<ServiceProvider>

    @POST("api/requests")
    suspend fun createServiceRequest(
        @Body request: ServiceRequestCreate
    ): Response<ServiceRequest>

    @GET("api/requests/user/{userId}")
    suspend fun getUserRequests(
        @Path("userId") userId: String
    ): Response<List<ServiceRequest>>

    @PUT("api/user/settings")
    suspend fun updateSettings(
        @Body settings: UserSettings
    ): Response<UserSettings>
}
