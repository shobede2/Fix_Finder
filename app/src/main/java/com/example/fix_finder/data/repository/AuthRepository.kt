package com.example.fix_finder.data.repository

import com.example.fix_finder.data.api.ApiClient
import com.example.fix_finder.data.api.FixFinderApiService
import com.example.fix_finder.data.local.SessionManager
import com.example.fix_finder.data.model.*
import com.google.gson.Gson

class AuthRepository(
    private val apiService: FixFinderApiService = ApiClient.apiService,
    private val sessionManager: SessionManager
) {
    private val gson = Gson()

    companion object {
        fun validatePassword(password: String): String? {
            if (password.isBlank()) {
                return "Password is required."
            }
            if (password.length <= 6) {
                return "Password must be greater than 6 characters."
            }
            if (!password.any { it.isUpperCase() }) {
                return "Password must contain at least one uppercase letter."
            }
            if (!password.any { it.isLowerCase() }) {
                return "Password must contain at least one lowercase letter."
            }
            val specialChars = "!@#$%^&*()_+-=[]{}|;:',.<>?/~`\"\\"
            if (!password.any { it in specialChars || !it.isLetterOrDigit() }) {
                return "Password must contain at least one special character."
            }
            return null
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Please enter both email/username and password.")
        }
        val pwdError = validatePassword(password)
        if (pwdError != null) {
            return AuthResult.Error(pwdError)
        }
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val loginResp = response.body()!!
                if (loginResp.success && loginResp.user != null && loginResp.token != null) {
                    sessionManager.saveAuthSession(loginResp.user, loginResp.token)
                    AuthResult.Success(loginResp.user, loginResp.token)
                } else {
                    AuthResult.Error(loginResp.message)
                }
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                AuthResult.Error(errorMsg ?: "Authentication failed. Please try again.")
            }
        } catch (e: Exception) {
            AuthResult.Error("Network error: ${e.localizedMessage ?: "Unable to connect to server."}")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String = User.ROLE_CUSTOMER,
        phone: String = "",
        location: String = "Polokwane, Limpopo"
    ): AuthResult {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Please fill in all required fields.")
        }
        if (!email.contains("@")) {
            return AuthResult.Error("Please enter a valid email address.")
        }
        val pwdError = validatePassword(password)
        if (pwdError != null) {
            return AuthResult.Error(pwdError)
        }

        return try {
            val request = RegisterRequest(name, email, password, role, phone, location)
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                val regResp = response.body()!!
                if (regResp.success && regResp.user != null && regResp.token != null) {
                    sessionManager.saveAuthSession(regResp.user, regResp.token)
                    AuthResult.Success(regResp.user, regResp.token)
                } else {
                    AuthResult.Error(regResp.message)
                }
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                AuthResult.Error(errorMsg ?: "Registration failed. Please try again.")
            }
        } catch (e: Exception) {
            AuthResult.Error("Network error: ${e.localizedMessage ?: "Unable to connect to server."}")
        }
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun getCurrentUser(): User? = sessionManager.getUser()

    fun logout() {
        sessionManager.logout()
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
