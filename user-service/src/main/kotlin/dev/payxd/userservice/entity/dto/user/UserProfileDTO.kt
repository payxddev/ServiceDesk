package dev.payxd.userservice.entity.dto.user

import dev.payxd.userservice.entity.Role

data class UserProfileDTO(
    val id: Long,
    val username: String,
    val email: String,
    val firstname: String,
    val lastname: String,
    val roles: Set<Role>,
    val verified: Boolean
)
