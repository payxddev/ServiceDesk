package dev.payxd.userservice.service.auth

import dev.payxd.userservice.entity.User
import dev.payxd.userservice.entity.dto.ApiResponse
import dev.payxd.userservice.entity.dto.auth.RegistrationDTO
import dev.payxd.userservice.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class RegistrationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun registerUser(request: RegistrationDTO): ResponseEntity<ApiResponse> {
        if (userRepository.existsByEmail(request.email)) {
            if (userRepository.existsByUsername(request.username)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse("Username is already used"))
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse("Email is already used"))
        }

        val user = User(
            id = null,
            username = request.username,
            firstname = request.firstname,
            lastname = request.lastname,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            verified = false
        )

        userRepository.save(user)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse("User Created"))
    }
}
