package com.copago.petglam.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
class JwtConfig {
    var secret: String = "default-jwt-secret-key-should-be-changed-in-production"
    var accessTokenExpiration: Duration = Duration.ofHours(1)
    var refreshTokenExpiration: Duration = Duration.ofDays(30)
    var issuer: String = "petglam-api"

    var excludePaths: List<String> = listOf(
        "/api/v1/oauth2/**",
        "/api/v1/auth/**",
        "/api/v1/salons/**",
        "/h2-console/**",
        "swagger-ui/**",
        "/swagger-resources/**",
        "/v3/api-docs/**",
        "actuator/**",
        "/app/chat/**",
        "/ws/chat/**"

    )
}
