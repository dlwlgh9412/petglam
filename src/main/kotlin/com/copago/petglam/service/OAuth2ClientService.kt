package com.copago.petglam.service

import com.copago.petglam.client.OAuth2ApiClient
import com.copago.petglam.context.PetglamRequestContext
import com.copago.petglam.exception.OAuth2Exception
import com.copago.petglam.model.UserProfile
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OAuth2ClientService(
    private val oAuth2Clients: List<OAuth2ApiClient>
) {
    private val log = LoggerFactory.getLogger(OAuth2ClientService::class.java)
    
    /**
     * 소셜 로그인 URL 생성
     */
    fun generateLoginUrl(provider: String): String {
        val client = getClientByProvider(provider)
        val state = UUID.randomUUID().toString()
        log.info("Generating OAuth2 login URL for provider: {} [requestId={}]", 
            provider, PetglamRequestContext.getRequestId())
        return client.generateLoginUrl(state)
    }

    /**
     * 인증 코드로 소셜 로그인 처리
     */
    fun processOAuth2Login(provider: String, code: String, state: String? = null): UserProfile {
        val requestId = PetglamRequestContext.getRequestId()
        log.info("Processing OAuth2 login for provider: {} [requestId={}]", provider, requestId)
        
        val client = getClientByProvider(provider)
        
        try {
            val tokenInfo = client.getToken(code)
            return client.getUserProfile(tokenInfo.accessToken)
        } catch (e: Exception) {
            log.error("OAuth2 login processing failed for provider: {} [requestId={}]", 
                provider, requestId, e)
            
            throw when (e) {
                is OAuth2Exception -> e
                else -> OAuth2Exception.userProfileFetchFailed(
                    provider = provider,
                    errorMessage = e.message ?: "알 수 없는 오류",
                    cause = e
                )
            }
        }
    }

    /**
     * 공급자 이름으로 OAuth2ApiClient 찾기
     */
    private fun getClientByProvider(provider: String): OAuth2ApiClient {
        val availableProviders = oAuth2Clients.map { it.getProviderName() }
        
        return oAuth2Clients.find { it.getProviderName().equals(provider, ignoreCase = true) }
            ?: throw OAuth2Exception.unsupportedProvider(
                provider = provider,
                availableProviders = availableProviders
            )
    }
}
