package com.example.fix_finder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

class RegisterFragment : Fragment() {

    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager.getInstance(requireContext())
        authRepository = AuthRepository(sessionManager = sessionManager)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val rbTechnician = view.findViewById<RadioButton>(R.id.rbTechnician)
        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etRegEmail)
        val etPhone = view.findViewById<TextInputEditText>(R.id.etPhone)
        val etLocation = view.findViewById<TextInputEditText>(R.id.etLocation)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etRegPassword)

        val tilName = view.findViewById<TextInputLayout>(R.id.tilName)
        val tilEmail = view.findViewById<TextInputLayout>(R.id.tilRegEmail)
        val tilPassword = view.findViewById<TextInputLayout>(R.id.tilRegPassword)

        val btnRegisterSubmit = view.findViewById<MaterialButton>(R.id.btnRegisterSubmit)
        val tvRegError = view.findViewById<TextView>(R.id.tvRegError)
        val pbRegLoading = view.findViewById<ProgressBar>(R.id.pbRegLoading)

        btnBack?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        etName?.addTextChangedListener { tilName?.error = null; tvRegError?.visibility = View.GONE }
        etEmail?.addTextChangedListener { tilEmail?.error = null; tvRegError?.visibility = View.GONE }
        etPassword?.addTextChangedListener { tilPassword?.error = null; tvRegError?.visibility = View.GONE }

        btnRegisterSubmit?.setOnClickListener {
            val name = etName?.text?.toString()?.trim().orEmpty()
            val email = etEmail?.text?.toString()?.trim().orEmpty()
            val phone = etPhone?.text?.toString()?.trim().orEmpty()
            val location = etLocation?.text?.toString()?.trim().orEmpty()
            val password = etPassword?.text?.toString()?.trim().orEmpty()
            val role = if (rbTechnician?.isChecked == true) User.ROLE_TECHNICIAN else User.ROLE_CUSTOMER

            var isValid = true
            if (name.isBlank()) {
                tilName?.error = "Full Name is required"
                isValid = false
            }
            if (email.isBlank()) {
                tilEmail?.error = "Email address is required"
                isValid = false
            } else if (!email.contains("@")) {
                tilEmail?.error = "Please enter a valid email address"
                isValid = false
            }
            val pwdError = AuthRepository.validatePassword(password)
            if (pwdError != null) {
                tilPassword?.error = pwdError
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            pbRegLoading?.visibility = View.VISIBLE
            btnRegisterSubmit.isEnabled = false
            tvRegError?.visibility = View.GONE

            lifecycleScope.launch {
                val result = authRepository.register(
                    name = name,
                    email = email,
                    password = password,
                    role = role,
                    phone = phone,
                    location = location
                )

                pbRegLoading?.visibility = View.GONE
                btnRegisterSubmit.isEnabled = true

                when (result) {
                    is AuthResult.Success -> {
                        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack() // go back to login or open home
                        val targetFragment = if (result.user.role == User.ROLE_TECHNICIAN) DashboardFragment() else HomeFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, targetFragment)
                            .commit()
                    }
                    is AuthResult.Error -> {
                        tvRegError?.text = result.message
                        tvRegError?.visibility = View.VISIBLE
                    }
                    AuthResult.Loading -> {}
                }
            }
        }
    }
}
