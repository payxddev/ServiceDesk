package dev.payxd.userservice.service.auth

import dev.payxd.userservice.config.jwt.JwtService
import dev.payxd.userservice.entity.dto.auth.AuthResponse
import dev.payxd.userservice.entity.dto.auth.AuthenticationDTO
import dev.payxd.userservice.entity.dto.user.UserProfileDTO
import dev.payxd.userservice.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) {

    fun authenticateUser(request: AuthenticationDTO): AuthResponse {
        val username = request.username
        val password = request.password

        // Аутентификация пользователя
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
            username,
            password
        )
        authenticationManager.authenticate(usernamePasswordAuthenticationToken)

        // Генерация токенов
        val accessToken = jwtService.generateAccessToken(username)
        val refreshToken = jwtService.generateRefreshToken(username)

        // Поиск пользователя
        val user = userRepository.findUserByUsername(username)
            ?: throw UsernameNotFoundException("User with username $username not found")

        // Формирование DTO профиля пользователя с множественными ролями
        val userProfile = UserProfileDTO(
            id = user.id!!,
            username = user.username,
            email = user.email,
            firstname = user.firstname,
            lastname = user.lastname,
            roles = user.roles,  // Теперь здесь используется Set<Role>
            verified = user.verified
        )

        // Возврат ответа с токенами и профилем пользователя
        return AuthResponse(accessToken, refreshToken, userProfile)
    }
}
