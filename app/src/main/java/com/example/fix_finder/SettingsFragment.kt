package com.example.fix_finder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.fix_finder.data.local.SessionManager
import com.example.fix_finder.data.model.User
import com.example.fix_finder.data.model.UserSettings
import com.example.fix_finder.data.repository.SettingsRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager.getInstance(requireContext())
        settingsRepository = SettingsRepository(sessionManager = sessionManager)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvProfileEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val tvProfileRole = view.findViewById<TextView>(R.id.tvProfileRole)

        val switchPush = view.findViewById<SwitchCompat>(R.id.switchPush)
        val switchEmail = view.findViewById<SwitchCompat>(R.id.switchEmail)
        val switchSms = view.findViewById<SwitchCompat>(R.id.switchSms)

        val spLocation = view.findViewById<Spinner>(R.id.spLocation)
        val rgTheme = view.findViewById<RadioGroup>(R.id.rgTheme)
        val rbThemeSystem = view.findViewById<RadioButton>(R.id.rbThemeSystem)
        val rbThemeLight = view.findViewById<RadioButton>(R.id.rbThemeLight)
        val rbThemeDark = view.findViewById<RadioButton>(R.id.rbThemeDark)

        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)

        btnBack?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Display current user profile info
        val user = sessionManager.getUser()
        if (user != null) {
            tvProfileName?.text = user.name
            tvProfileEmail?.text = user.email
            tvProfileRole?.text = "Role: ${user.role.replaceFirstChar { it.uppercase() }}"
        } else {
            tvProfileName?.text = "Guest User"
            tvProfileEmail?.text = "Not logged in"
            tvProfileRole?.text = "Role: Guest"
        }

        // Location Spinner adapter
        val locations = listOf("Polokwane, Limpopo", "Johannesburg, Gauteng", "Pretoria, Gauteng", "Cape Town, Western Cape", "Durban, KwaZulu-Natal")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, locations)
        spLocation?.adapter = adapter

        // Load current settings
        val currentSettings = settingsRepository.getSettings()
        switchPush?.isChecked = currentSettings.pushNotifications
        switchEmail?.isChecked = currentSettings.emailAlerts
        switchSms?.isChecked = currentSettings.smsAlerts

        val locationIndex = locations.indexOf(currentSettings.preferredLocation).coerceAtLeast(0)
        spLocation?.setSelection(locationIndex)

        when (currentSettings.themeMode) {
            UserSettings.THEME_LIGHT -> rbThemeLight?.isChecked = true
            UserSettings.THEME_DARK -> rbThemeDark?.isChecked = true
            else -> rbThemeSystem?.isChecked = true
        }

        // Save settings whenever changed
        val saveAction = {
            val themeMode = when (rgTheme?.checkedRadioButtonId) {
                R.id.rbThemeLight -> UserSettings.THEME_LIGHT
                R.id.rbThemeDark -> UserSettings.THEME_DARK
                else -> UserSettings.THEME_SYSTEM
            }

            MainActivity.applyTheme(themeMode)

            val newSettings = UserSettings(
                userId = user?.id ?: "guest",
                pushNotifications = switchPush?.isChecked ?: true,
                emailAlerts = switchEmail?.isChecked ?: true,
                smsAlerts = switchSms?.isChecked ?: false,
                preferredLocation = spLocation?.selectedItem?.toString() ?: locations[0],
                themeMode = themeMode
            )

            lifecycleScope.launch {
                settingsRepository.saveSettings(newSettings)
            }
        }

        switchPush?.setOnCheckedChangeListener { _, _ -> saveAction() }
        switchEmail?.setOnCheckedChangeListener { _, _ -> saveAction() }
        switchSms?.setOnCheckedChangeListener { _, _ -> saveAction() }
        rgTheme?.setOnCheckedChangeListener { _, _ -> saveAction() }

        // Logout handling
        btnLogout?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of Fix Finder?")
                .setPositiveButton("Log Out") { _, _ ->
                    sessionManager.logout()
                    Toast.makeText(context, "Logged out successfully.", Toast.LENGTH_SHORT).show()

                    // Clear fragment back stack and return to LoginFragment
                    parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, LoginFragment())
                        .commit()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Bottom Navigation Bar
        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        if (user?.role == User.ROLE_TECHNICIAN) {
            bottomNav?.menu?.clear()
            bottomNav?.inflateMenu(R.menu.technician_bottom_menu)
            bottomNav?.selectedItemId = R.id.nav_tech_profile
            bottomNav?.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_dashboard -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, DashboardFragment())
                            .commit()
                        true
                    }
                    R.id.nav_jobs, R.id.nav_earnings -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, BookingsFragment())
                            .commit()
                        true
                    }
                    R.id.nav_tech_profile -> true
                    else -> false
                }
            }
        } else {
            bottomNav?.selectedItemId = R.id.nav_profile
            bottomNav?.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, HomeFragment())
                            .commit()
                        true
                    }
                    R.id.nav_search -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, SearchFragment())
                            .commit()
                        true
                    }
                    R.id.nav_bookings, R.id.nav_messages -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, BookingsFragment())
                            .commit()
                        true
                    }
                    R.id.nav_profile -> true
                    else -> false
                }
            }
        }
    }
}
