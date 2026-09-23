package com.example.fix_finder

import com.example.fix_finder.data.api.FixFinderApiService
import com.example.fix_finder.data.model.UserSettings
import com.example.fix_finder.data.repository.ResultState
import com.example.fix_finder.data.repository.SettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SettingsRepositoryTest {

    private lateinit var fakeApiService: AuthRepositoryTest.FakeApiService
    private lateinit var fakeSessionManager: AuthRepositoryTest.FakeSessionManager
    private lateinit var settingsRepository: SettingsRepository

    @Before
    fun setUp() {
        fakeApiService = AuthRepositoryTest.FakeApiService()
        fakeSessionManager = AuthRepositoryTest.FakeSessionManager()
        settingsRepository = SettingsRepository(fakeApiService, fakeSessionManager)
    }

    @Test
    fun getSettings_returnsDefaultSettings() {
        val settings = settingsRepository.getSettings()
        assertNotNull(settings)
        assertTrue(settings.pushNotifications)
        assertEquals("Polokwane, Limpopo", settings.preferredLocation)
    }

    @Test
    fun saveSettings_updatesSettingsSuccessfully() = runTest {
        val newSettings = UserSettings(
            userId = "usr_101",
            pushNotifications = false,
            emailAlerts = true,
            smsAlerts = true,
            preferredLocation = "Cape Town, Western Cape",
            themeMode = UserSettings.THEME_DARK
        )

        val result = settingsRepository.saveSettings(newSettings)
        assertTrue(result is ResultState.Success)
        val saved = (result as ResultState.Success).data
        assertEquals("Cape Town, Western Cape", saved.preferredLocation)
        assertEquals(UserSettings.THEME_DARK, saved.themeMode)
        assertFalse(saved.pushNotifications)
    }
}
