package com.example.fix_finder.data.repository

import com.example.fix_finder.data.api.ApiClient
import com.example.fix_finder.data.api.FixFinderApiService
import com.example.fix_finder.data.model.*
import com.google.gson.Gson

sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val message: String) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
}

class ServiceRepository(
    private val apiService: FixFinderApiService = ApiClient.apiService
) {
    private val gson = Gson()

    suspend fun getCategories(): ResultState<List<ServiceCategory>> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                ResultState.Error("Failed to fetch service categories.")
            }
        } catch (e: Exception) {
            ResultState.Error("Network error: ${e.localizedMessage ?: "Connection failed"}")
        }
    }

    suspend fun getProviders(
        query: String? = null,
        category: String? = null,
        location: String? = null
    ): ResultState<List<ServiceProvider>> {
        return try {
            val response = apiService.getProviders(query, category, location)
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                ResultState.Error("Failed to fetch service providers.")
            }
        } catch (e: Exception) {
            ResultState.Error("Network error: ${e.localizedMessage ?: "Connection failed"}")
        }
    }

    suspend fun getProviderById(id: String): ResultState<ServiceProvider> {
        return try {
            val response = apiService.getProviderById(id)
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                ResultState.Error("Service provider not found.")
            }
        } catch (e: Exception) {
            ResultState.Error("Network error: ${e.localizedMessage ?: "Connection failed"}")
        }
    }

    suspend fun createServiceRequest(request: ServiceRequestCreate): ResultState<ServiceRequest> {
        if (request.description.isBlank()) {
            return ResultState.Error("Please describe the problem or service required.")
        }
        if (request.address.isBlank()) {
            return ResultState.Error("Please enter a valid service location.")
        }

        return try {
            val response = apiService.createServiceRequest(request)
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                ResultState.Error(errorMsg ?: "Failed to submit service request.")
            }
        } catch (e: Exception) {
            ResultState.Error("Network error: ${e.localizedMessage ?: "Connection failed"}")
        }
    }

    suspend fun getUserRequests(userId: String): ResultState<List<ServiceRequest>> {
        return try {
            val response = apiService.getUserRequests(userId)
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                ResultState.Error("Failed to load service requests.")
            }
        } catch (e: Exception) {
            ResultState.Error("Network error: ${e.localizedMessage ?: "Connection failed"}")
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val map = gson.fromJson(errorBody, Map::class.java)
            (map["message"] ?: map["error"])?.toString()
        } catch (e: Exception) {
            null
        }
    }
}
