package dev.payxd.userservice.config.jwt

import dev.payxd.userservice.config.security.CustomUserDetailsService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtService(
    private val customUserDetailsService: CustomUserDetailsService,
    @Value("\${jwt.access.secret}") private val accessSecret: String,
    @Value("\${jwt.refresh.secret}") private val refreshSecret: String,
    @Value("\${jwt.access.token-duration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh.token-duration}") private val refreshTokenExpiration: Long
) {

    fun generateAccessToken(username: String): String {
        return generateToken(username, accessTokenExpiration, accessSecret, "ACCESS")
    }

    fun generateRefreshToken(username: String): String {
        return generateToken(username, refreshTokenExpiration, refreshSecret, "REFRESH")
    }

    fun extractUserNameFromToken(token: String, isAccessToken: Boolean): String? {
        val secret = if (isAccessToken) accessSecret else refreshSecret
        return extractClaim(token, secret) { it.subject }
    }
    private fun generateToken(username: String, expiration: Long, secretKey: String, tokenType: String): String {
        val userDetails = customUserDetailsService.loadUserByUsername(username)

        val now = Date()
        val validity = Date(now.time + expiration )

        return Jwts.builder()
            .subject(userDetails.username)
            .claim("tokenType", tokenType)
            .issuedAt(now)
            .expiration(validity)
            .signWith(getSignInKey(secretKey), Jwts.SIG.HS256)
            .compact()
    }


    private fun getSignInKey(secretKey: String): SecretKey {
        return Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    private fun extractAllClaims(token: String, secret: String): Claims {
        return Jwts.parser()
            .verifyWith(getSignInKey(secret))
            .build()
            .parseSignedClaims(token)
            .body
    }

    private fun <T> extractClaim(token: String, secret: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token, secret)
        return claimsResolver.invoke(claims)
    }

    fun isTokenValid(token: String, username: String, isAccessToken: Boolean): Boolean {
        val secret = if (isAccessToken) accessSecret else refreshSecret
        val userNameFromToken = extractUserNameFromToken(token, isAccessToken)
        return userNameFromToken == username && !isTokenExpired(token, secret)
    }

    private fun isTokenExpired(token: String, secret: String): Boolean {
        return extractExpirationDate(token, secret).before(Date())
    }

    private fun extractExpirationDate(token: String, secret: String): Date {
        return extractClaim(token, secret) { it.expiration }
    }

    fun isAccessToken(token: String): Boolean {
        val claims = extractAllClaims(token, accessSecret)
        return claims["tokenType"] == "ACCESS"
    }

    fun isRefreshToken(token: String): Boolean {
        val claims = extractAllClaims(token, refreshSecret)
        return claims["tokenType"] == "REFRESH"
    }
}