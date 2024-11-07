package dev.payxd.userservice.entity.dto.auth

data class RegistrationDTO(
    val username: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String
)