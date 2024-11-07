package dev.payxd.userservice.entity.dto.auth

data class TokenValidationDTO(
    val id: Long?,
    val username: String,
    val roles: Set<String>  // Теперь передаем коллекцию ролей в виде Set<String>
)