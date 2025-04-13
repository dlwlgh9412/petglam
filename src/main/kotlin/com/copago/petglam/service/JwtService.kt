package com.copago.petglam.service

import com.copago.petglam.config.JwtConfig
import com.copago.petglam.context.RequestContextHolder
import com.copago.petglam.exception.AuthenticationException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(private val jwtConfig: JwtConfig) {
    private val log = LoggerFactory.getLogger(JwtService::class.java)
    private lateinit var secretKey: SecretKey

    @PostConstruct
    fun init() {
        secretKey = Keys.hmacShaKeyFor(jwtConfig.secret.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * 액세스 토큰 생성
     */
    fun generateAccessToken(userId: String, email: String?, roles: List<String> = listOf("USER")): String {
        return generateToken(userId, email, roles, jwtConfig.accessTokenExpiration.toMillis())
    }

    /**
     * 리프레시 토큰 생성
     */
    fun generateRefreshToken(userId: String): String {
        return generateToken(userId, null, listOf(), jwtConfig.refreshTokenExpiration.toMillis())
    }

    /**
     * JWT 토큰 생성
     */
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

    /**
     * 토큰 검증 및 클레임 추출
     */
    fun validateToken(token: String): Claims {
        val requestId = RequestContextHolder.getRequestId()

        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            log.debug("Expired JWT token [requestId={}]", requestId, e)
            throw AuthenticationException.tokenExpired(
                errorDetails = mapOf("token" to maskToken(token))
            )
        } catch (e: JwtException) {
            log.debug("Invalid JWT token [requestId={}]", requestId, e)
            throw AuthenticationException.invalidToken(
                message = "유효하지 않은 토큰입니다: ${e.message}",
                errorDetails = mapOf("token" to maskToken(token)),
                cause = e
            )
        } catch (e: IllegalArgumentException) {
            log.debug("JWT token compact of handler are invalid [requestId={}]", requestId, e)
            throw AuthenticationException.invalidToken(
                message = "유효하지 않은 토큰입니다: ${e.message}",
                errorDetails = mapOf("token" to maskToken(token)),
                cause = e
            )
        }
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    fun getUserIdFromToken(token: String): String {
        return validateToken(token).subject
    }

    /**
     * 토큰에서 이메일 추출
     */
    fun getEmailFromToken(token: String): String? {
        return validateToken(token).get("email", String::class.java)
    }

    /**
     * 토큰에서 사용자 역할 목록 추출
     */
    fun getRolesFromToken(token: String): List<String> {
        @Suppress("UNCHECKED_CAST")
        return validateToken(token).get("roles", List::class.java) as List<String>
    }

    /**
     * 토큰 마스킹 처리 (로깅용)
     */
    private fun maskToken(token: String): String {
        return if (token.length > 10) {
            val prefix = token.substring(0, 5)
            val suffix = token.substring(token.length - 5)
            "$prefix....$suffix"
        } else {
            "***"
        }
    }
}