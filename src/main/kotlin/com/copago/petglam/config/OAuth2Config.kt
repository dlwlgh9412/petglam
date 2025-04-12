package com.copago.petglam.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "oauth2")
class OAuth2Config {
    var kakao: KakaoConfig = KakaoConfig()

    class KakaoConfig {
        var clientId: String = ""
        var clientSecret: String = ""
        var redirectUri: String = ""
        var authUri: String = ""
        var tokenUri: String = ""
        var userInfoUri: String = ""
        var scopes: String = ""
    }
}