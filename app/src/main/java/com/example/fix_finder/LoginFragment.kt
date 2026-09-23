package com.example.fix_finder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fix_finder.data.local.SessionManager
import com.example.fix_finder.data.model.AuthResult
import com.example.fix_finder.data.model.User
import com.example.fix_finder.data.repository.AuthRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager.getInstance(requireContext())
        authRepository = AuthRepository(sessionManager = sessionManager)

        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val tilEmail = view.findViewById<TextInputLayout>(R.id.tilEmail)
        val tilPassword = view.findViewById<TextInputLayout>(R.id.tilPassword)
        val btnLogin = view.findViewById<MaterialButton>(R.id.btnLogin)
        val tvError = view.findViewById<TextView>(R.id.tvLoginError)
        val pbLoading = view.findViewById<ProgressBar>(R.id.pbLoginLoading)
        val btnRegister = view.findViewById<TextView>(R.id.btnGoRegister)

        etEmail?.addTextChangedListener {
            tilEmail?.error = null
            tvError?.visibility = View.GONE
        }

        etPassword?.addTextChangedListener {
            tilPassword?.error = null
            tvError?.visibility = View.GONE
        }

        btnLogin?.setOnClickListener {
            val email = etEmail?.text?.toString()?.trim().orEmpty()
            val password = etPassword?.text?.toString()?.trim().orEmpty()

            var isValid = true
            if (email.isBlank()) {
                tilEmail?.error = "Email or username is required"
                isValid = false
            }
            val pwdError = AuthRepository.validatePassword(password)
            if (pwdError != null) {
                tilPassword?.error = pwdError
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            // Perform REST login request
            pbLoading?.visibility = View.VISIBLE
            btnLogin.isEnabled = false
            tvError?.visibility = View.GONE

            lifecycleScope.launch {
                val result = authRepository.login(email, password)
                pbLoading?.visibility = View.GONE
                btnLogin.isEnabled = true

                when (result) {
                    is AuthResult.Success -> {
                        Toast.makeText(context, "Welcome back, ${result.user.name}!", Toast.LENGTH_SHORT).show()
                        navigateToDestination(result.user)
                    }
                    is AuthResult.Error -> {
                        tvError?.text = result.message
                        tvError?.visibility = View.VISIBLE
                    }
                    AuthResult.Loading -> {
                        // Already handled
                    }
                }
            }
        }

        btnRegister?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun navigateToDestination(user: User) {
        val targetFragment = if (user.role == User.ROLE_TECHNICIAN) {
            DashboardFragment()
        } else {
            HomeFragment()
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, targetFragment)
            .commit()
    }
}
