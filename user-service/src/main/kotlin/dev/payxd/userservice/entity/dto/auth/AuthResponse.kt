package dev.payxd.userservice.entity.dto.auth

import dev.payxd.userservice.entity.dto.user.UserProfileDTO

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserProfileDTO
)