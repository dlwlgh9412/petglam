package com.copago.petglam.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtConfig {
    var secret: String = "default-jwt-secret-key-should-be-changed-in-production"
    var accessTokenExpiration: Duration = Duration.ofHours(1)
    var refreshTokenExpiration: Duration = Duration.ofDays(30)
    var issuer: String = "petglam-api"
}
