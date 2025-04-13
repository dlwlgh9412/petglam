package com.copago.petglam.service

import com.copago.petglam.config.JwtConfig
import com.copago.petglam.exception.UnauthorizedException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(private val jwtConfig: JwtConfig) {

    private lateinit var secretKey: SecretKey

    @PostConstruct
    fun init() {
        secretKey = Keys.hmacShaKeyFor(jwtConfig.secret.toByteArray(StandardCharsets.UTF_8))
    }

    fun generateAccessToken(userId: String, email: String?, roles: List<String> = listOf("USER")): String {
        return generateToken(userId, email, roles, jwtConfig.accessTokenExpiration.toMillis())
    }

    fun generateRefreshToken(userId: String): String {
        return generateToken(userId, null, listOf(), jwtConfig.refreshTokenExpiration.toMillis())
    }

    private fun generateToken(userId: String, email: String?, roles: List<String>, expirationMillis: Long): String {
        val now = Date()
        val expiration = Date(now.time + expirationMillis)

        val claims = Jwts.claims()
            .subject(userId)
            .issuedAt(now)
            .expiration(expiration)
            .add("roles", roles)
            .build()

        return Jwts.builder()
            .claims(claims)
            .issuer(jwtConfig.issuer)
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Claims {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: JwtException) {
            throw UnauthorizedException("유효하지 않은 토큰입니다: ${e.message}")
        } catch (e: IllegalArgumentException) {
            throw UnauthorizedException("유효하지 않은 토큰입니다: ${e.message}")
        }
    }

    fun getUserIdFromToken(token: String): String {
        return validateToken(token).subject
    }

    fun getEmailFromToken(token: String): String? {
        return validateToken(token).get("email", String::class.java)
    }

    fun getRolesFromToken(token: String): List<String> {
        @Suppress("UNCHECKED_CAST")
        return validateToken(token).get("roles", List::class.java) as List<String>
    }
}
