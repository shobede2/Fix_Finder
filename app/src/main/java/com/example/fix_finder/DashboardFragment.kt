package com.example.fix_finder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fix_finder.data.local.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardFragment : Fragment() {

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager.getInstance(requireContext())
        val user = sessionManager.getUser()
        val tvTechGreeting = view.findViewById<TextView>(R.id.tvTechGreeting)
        if (user != null) {
            tvTechGreeting?.text = "Good morning, ${user.name.substringBefore(" ")} 👋"
        }

        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav?.selectedItemId = R.id.nav_dashboard
        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true
                R.id.nav_jobs, R.id.nav_earnings -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, BookingsFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_tech_profile -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SettingsFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
