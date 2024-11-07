package dev.payxd.gateway.config

import dev.payxd.gateway.TokenValidationDTO
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class AuthFilter(private val webClient: WebClient.Builder) : AbstractGatewayFilterFactory<AuthFilter.Config>(Config::class.java) {

    private val permittedEndpoints = setOf(
        "/auth/register",
        "/auth/login"
    )

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val jwtToken = extractToken(exchange.request)

            if (checkEndpoint(exchange.request)) {
                return@GatewayFilter chain.filter(exchange)
            }

            if (jwtToken.isEmpty()) {
                throw RuntimeException("Missing Authorization token")
            }

            // Валидация токена через микросервис
            webClient.build()
                .post()
                .uri("lb://user-service/auth/validate?token=${jwtToken.substring(7)}")
                .retrieve()
                .bodyToMono<TokenValidationDTO>()
                .flatMap { dto ->
                    val mutatedRequest = exchange.request.mutate()
                        .header("auth-header-id", dto.id.toString())
                        .header("user-name", dto.username)
                        .header("user-roles", dto.roles?.joinToString(",") ?: "")
                        .build()

                    chain.filter(exchange.mutate().request(mutatedRequest).build())
                }
                .onErrorResume { e ->
                    // Обработка ошибок в случае неудачной валидации
                    throw RuntimeException("Token validation failed: ${e.message}")
                }
        }
    }

    class Config

    private fun extractToken(request: ServerHttpRequest): String {
        return request.headers.getFirst("Authorization").orEmpty()
    }

    private fun checkEndpoint(request: ServerHttpRequest): Boolean {
        return permittedEndpoints.any { request.path.toString().startsWith(it) }
    }
}
