package dev.payxd.userservice.config.jwt

import dev.payxd.userservice.config.security.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter


@Service
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)

        // Проверяем тип токена
        if (!jwtService.isAccessToken(jwt)) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Invalid token type")
            return
        }

        val userName = jwtService.extractUserNameFromToken(jwt, isAccessToken = true)
        if (userName != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = customUserDetailsService.loadUserByUsername(userName)
            if (jwtService.isTokenValid(jwt, userName, isAccessToken = true)) {
                val authenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authenticationToken
            }
        }
        filterChain.doFilter(request, response)
    }
}