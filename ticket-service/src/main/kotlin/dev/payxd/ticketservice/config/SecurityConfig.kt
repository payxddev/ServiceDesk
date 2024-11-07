package dev.payxd.ticketservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtRequestFilter: JwtRequestFilter)  {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val corsConfiguration = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:3000","http://localhost:8080")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }


        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return         http
            .cors { it.configurationSource(source) }
            .csrf { it.disable() }
            .authorizeHttpRequests{
                it.requestMatchers("/tickets/**").hasAnyRole("USER", "ADMIN")
                it.anyRequest().authenticated()
            }
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:3000","http://localhost:8080")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}
