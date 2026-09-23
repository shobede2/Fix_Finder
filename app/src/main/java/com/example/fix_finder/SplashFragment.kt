package com.example.fix_finder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fix_finder.data.local.SessionManager
import com.example.fix_finder.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(1200)
            if (!isAdded) return@launch

            val sessionManager = SessionManager.getInstance(requireContext())
            val targetFragment = if (sessionManager.isLoggedIn()) {
                val user = sessionManager.getUser()
                if (user?.role == User.ROLE_TECHNICIAN) DashboardFragment() else HomeFragment()
            } else {
                LoginFragment()
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, targetFragment)
                .commitAllowingStateLoss()
        }
    }
}
