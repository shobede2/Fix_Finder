package com.example.fix_finder.data.repository

import com.example.fix_finder.data.api.ApiClient
import com.example.fix_finder.data.api.FixFinderApiService
import com.example.fix_finder.data.local.SessionManager
import com.example.fix_finder.data.model.UserSettings

class SettingsRepository(
    private val apiService: FixFinderApiService = ApiClient.apiService,
    private val sessionManager: SessionManager
) {
    fun getSettings(): UserSettings {
        return sessionManager.getSettings()
    }

    suspend fun saveSettings(settings: UserSettings): ResultState<UserSettings> {
        sessionManager.saveSettings(settings)
        return try {
            val response = apiService.updateSettings(settings)
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                ResultState.Success(settings)
            }
        } catch (e: Exception) {
            // Local fallback
            ResultState.Success(settings)
        }
    }
}
