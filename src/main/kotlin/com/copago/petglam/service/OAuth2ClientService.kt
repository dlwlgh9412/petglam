package com.copago.petglam.service

import com.copago.petglam.client.OAuth2ApiClient
import com.copago.petglam.exception.OAuth2Exception
import com.copago.petglam.model.UserProfile
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OAuth2ClientService(
    private val oAuth2Clients: List<OAuth2ApiClient>
) {
    /**
     * 소셜 로그인 URL 생성
     */
    fun generateLoginUrl(provider: String): String {
        val client = getClientByProvider(provider)
        val state = UUID.randomUUID().toString()
        return client.generateLoginUrl(state)
    }

    /**
     * 인증 코드로 소셜 로그인 처리
     */
    fun processOAuth2Login(provider: String, code: String, state: String? = null): UserProfile {
        val client = getClientByProvider(provider)
        val tokenInfo = client.getToken(code)
        return client.getUserProfile(tokenInfo.accessToken)
    }

    /**
     * 공급자 이름으로 OAuth2ApiClient 찾기
     */
    private fun getClientByProvider(provider: String): OAuth2ApiClient {
        return oAuth2Clients.find { it.getProviderName().equals(provider, ignoreCase = true) }
            ?: throw OAuth2Exception(provider, "지원하지 않는 소셜 로그인 제공자입니다.")
    }
}
