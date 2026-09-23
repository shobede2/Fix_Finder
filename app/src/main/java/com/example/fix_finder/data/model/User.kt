package com.example.fix_finder.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String = ROLE_CUSTOMER, // "customer" or "technician"
    val phone: String = "",
    val location: String = "Polokwane, Limpopo",
    val avatarUrl: String? = null
) {
    companion object {
        const val ROLE_CUSTOMER = "customer"
        const val ROLE_TECHNICIAN = "technician"
    }
}
