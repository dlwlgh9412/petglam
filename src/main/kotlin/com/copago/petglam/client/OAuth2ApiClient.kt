package com.copago.petglam.client

import com.copago.petglam.model.OAuthTokenInfo
import com.copago.petglam.model.UserProfile

/**
 * OAuth2 공급자를 위한 API 클라이언트 인터페이스
 */
interface OAuth2ApiClient {
    /**
     * 로그인 URL 생성
     */
    fun generateLoginUrl(state: String): String
    
    /**
     * 액세스 토큰 요청
     */
    fun getToken(code: String): OAuthTokenInfo
    
    /**
     * 사용자 프로필 정보 요청
     */
    fun getUserProfile(accessToken: String): UserProfile
    
    /**
     * 공급자 이름 반환
     */
    fun getProviderName(): String
}
