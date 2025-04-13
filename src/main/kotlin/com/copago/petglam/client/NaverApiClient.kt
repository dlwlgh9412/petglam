package com.copago.petglam.client

import com.copago.petglam.model.UserProfile
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * 네이버 API 클라이언트 (예시)
 * 필요한 경우 활성화하고 구현할 수 있음
 */
@Component
@ConditionalOnProperty(name = ["oauth2.naver.enabled"], havingValue = "true")
class NaverApiClient(
    private val httpClient: HttpClient
) : OAuth2ApiClient {
    // 실제 구현시 네이버 설정을 위한 설정 클래스 주입 필요
    
    override fun generateLoginUrl(state: String): String {
        // 네이버 로그인 URL 생성 로직
        return "https://nid.naver.com/oauth2.0/authorize?..." 
    }
    
    override fun getToken(code: String): OAuthTokenInfo {
        // 네이버 토큰 요청 로직
        val response = httpClient.postFormUrlEncoded(
            uri = "https://nid.naver.com/oauth2.0/token",
            formData = mapOf(
                "grant_type" to "authorization_code",
                "client_id" to "YOUR_CLIENT_ID",
                "client_secret" to "YOUR_CLIENT_SECRET",
                "code" to code
            ),
            responseType = NaverTokenResponseDto::class.java
        )
        
        return OAuthTokenInfo(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresIn = response.expiresIn,
            tokenType = response.tokenType,
            scope = response.scope
        )
    }
    
    override fun getUserProfile(accessToken: String): UserProfile {
        // 네이버 사용자 정보 요청 로직
        val response = httpClient.get(
            uri = "https://openapi.naver.com/v1/nid/me",
            responseType = NaverUserInfoResponse::class.java,
            headers = mapOf("Authorization" to "Bearer $accessToken")
        )
        
        val profile = response.response
        return UserProfile(
            id = profile.id,
            name = profile.name,
            email = profile.email,
            picture = profile.profileImage,
            provider = getProviderName()
        )
    }
    
    override fun getProviderName(): String = "naver"
    
    // DTO 클래스
    data class NaverTokenResponseDto(
        @JsonProperty("access_token")
        val accessToken: String,
        
        @JsonProperty("refresh_token")
        val refreshToken: String,
        
        @JsonProperty("token_type")
        val tokenType: String,
        
        @JsonProperty("expires_in")
        val expiresIn: Int,
        
        val scope: String? = null
    )
    
    data class NaverUserInfoResponse(
        val resultcode: String,
        val message: String,
        val response: NaverProfile
    )
    
    data class NaverProfile(
        val id: String,
        val nickname: String,
        val name: String,
        val email: String,
        
        @JsonProperty("profile_image")
        val profileImage: String
    )
}
