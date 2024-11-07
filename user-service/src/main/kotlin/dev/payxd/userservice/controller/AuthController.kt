package dev.payxd.userservice.controller

import dev.payxd.userservice.entity.dto.ApiResponse
import dev.payxd.userservice.entity.dto.auth.*
import dev.payxd.userservice.service.auth.AuthenticationService
import dev.payxd.userservice.service.auth.RefreshTokenService
import dev.payxd.userservice.service.auth.RegistrationService
import dev.payxd.userservice.service.auth.TokenValidationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val registrationService: RegistrationService,
    private val tokenValidationService: TokenValidationService,
    private val refreshTokenService: RefreshTokenService,
) {


    @PostMapping("/register")
    fun register(@RequestBody request: RegistrationDTO): ResponseEntity<ApiResponse> = registrationService.registerUser(request)



    @PostMapping("/login")
    fun authenticate(@RequestBody request: AuthenticationDTO): AuthResponse = authenticationService.authenticateUser(request)

    @PostMapping("/validate")
    fun validateToken(@RequestParam token: String): ResponseEntity<TokenValidationDTO> = tokenValidationService.validateJwtToken(token)

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshTokenRequest): AuthResponse {
        return refreshTokenService.refreshAccessToken(request.refreshToken)
    }
}