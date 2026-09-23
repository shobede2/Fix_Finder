package com.example.fix_finder.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = User.ROLE_CUSTOMER,
    val phone: String = "",
    val location: String = "Polokwane, Limpopo"
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: User? = null
)

sealed class AuthResult {
    data class Success(val user: User, val token: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}
