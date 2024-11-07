package dev.payxd.userservice.entity.dto.user

data class UpdateUserRequest(
    val email: String?,
    val password: String?
)
