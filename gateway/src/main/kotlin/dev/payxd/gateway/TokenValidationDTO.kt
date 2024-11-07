package dev.payxd.gateway

data class TokenValidationDTO(
    val id: Long?,
    val username: String,
    val roles: Set<String>?  // Изменено на Set<String>
)