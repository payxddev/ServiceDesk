package dev.payxd.ticketservice.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtRequestFilter(private val restTemplate: RestTemplate) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        val token = authorizationHeader?.substring(7)

        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            try {
                val responseEntity = restTemplate.postForEntity(
                    "lb://user-service/auth/validate?token=$token",
                    null,
                    UserDetailsDTO::class.java
                )
                if (responseEntity.statusCode == HttpStatus.OK) {
                    val userDetails = responseEntity.body ?: throw RuntimeException("Invalid token")
                    val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            } catch (e: Exception) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Invalid token")
                return
            }
        }
        chain.doFilter(request, response)
    }
}