package dev.payxd.userservice.service.auth

import dev.payxd.userservice.config.jwt.JwtService
import dev.payxd.userservice.entity.dto.auth.TokenValidationDTO
import dev.payxd.userservice.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class TokenValidationService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) {

    fun validateJwtToken(token: String): ResponseEntity<TokenValidationDTO> {
        val username = jwtService.extractUserNameFromToken(token, isAccessToken = true).orEmpty()

        val user = userRepository.findUserByUsername(username)
            ?: throw UsernameNotFoundException("User with name $username not found")

        jwtService.isTokenValid(token, username, isAccessToken = true)
            .throwIfFalse("Token is not correct")

        val response = TokenValidationDTO(
            user.id,
            user.username,
            user.roles.map { it.name }.toSet()  // Передаем список ролей в виде Set<String>
        )
        return ResponseEntity.ok(response)
    }

    private fun Boolean.throwIfFalse(exceptionMessage: String) {
        if (!this) {
            throw RuntimeException(exceptionMessage)
        }
    }
}