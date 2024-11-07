package dev.payxd.userservice.service.auth

import dev.payxd.userservice.config.jwt.JwtService
import dev.payxd.userservice.entity.dto.auth.AuthResponse
import dev.payxd.userservice.entity.dto.user.UserProfileDTO
import dev.payxd.userservice.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class RefreshTokenService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) {

    fun refreshAccessToken(refreshToken: String): AuthResponse {
        val username = jwtService.extractUserNameFromToken(refreshToken, isAccessToken = false)
            ?: throw IllegalArgumentException("Invalid refresh token")

        val user = userRepository.findUserByUsername(username)
            ?: throw IllegalArgumentException("User not found")

        // Генерация нового access-токена
        val newAccessToken = jwtService.generateAccessToken(user.username)

        // Создание объекта UserProfileDTO с коллекцией ролей
        val userProfile = UserProfileDTO(
            id = user.id!!,
            username = user.username,
            email = user.email,
            firstname = user.firstname,
            lastname = user.lastname,
            roles = user.roles,  // Здесь используем Set<Role>
            verified = user.verified
        )

        // Возврат нового access-токена и профиля пользователя
        return AuthResponse(newAccessToken, refreshToken, userProfile)
    }
}