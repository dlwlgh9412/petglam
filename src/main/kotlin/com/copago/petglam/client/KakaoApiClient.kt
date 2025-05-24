package com.copago.petglam.client

import com.copago.petglam.config.OAuth2Config
import com.copago.petglam.context.PetglamRequestContext
import com.copago.petglam.dto.KakaoTokenResponse
import com.copago.petglam.dto.KakaoUserInfoResponse
import com.copago.petglam.exception.ExternalServiceException
import com.copago.petglam.model.OAuthTokenInfo
import com.copago.petglam.model.UserProfile
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class KakaoApiClient(
    private val httpClient: HttpClient,
    private val oAuth2Config: OAuth2Config
) : OAuth2ApiClient {
    private val log = LoggerFactory.getLogger(KakaoApiClient::class.java)

    /**
     * 카카오 액세스 토큰 요청
     */
    override fun getToken(code: String): OAuthTokenInfo {
        val requestId = PetglamRequestContext.getRequestId()
        log.info("Requesting Kakao access token [requestId={}]", requestId)

        val formData = mapOf(
            "grant_type" to "authorization_code",
            "client_id" to oAuth2Config.kakao.clientId,
            "client_secret" to oAuth2Config.kakao.clientSecret,
            "redirect_uri" to oAuth2Config.kakao.redirectUri,
            "code" to code
        )

        try {
            val response = httpClient.postFormUrlEncoded(
                uri = oAuth2Config.kakao.tokenUri,
                formData = formData,
                responseType = KakaoTokenResponse::class.java
            )

            return OAuthTokenInfo(
                accessToken = response.accessToken,
                tokenType = response.tokenType,
                refreshToken = response.refreshToken,
                expiresIn = response.expiresIn,
                scope = response.scope
            )
        } catch (e: Exception) {
            log.error("Failed to get Kakao token [requestId={}]", requestId, e)
            throw ExternalServiceException(
                serviceName = getProviderName(),
                message = "카카오 액세스 토큰 요청 중 오류가 발생했습니다.",
                isRetryable = false,
                details = mapOf("error" to (e.message ?: "알 수 없는 오류")),
                cause = e
            )
        }
    }

    /**
     * 카카오 사용자 프로필 요청
     */
    override fun getUserProfile(accessToken: String): UserProfile {
        val requestId = PetglamRequestContext.getRequestId()
        log.info("Requesting Kakao user profile [requestId={}]", requestId)

        try {
            val response = httpClient.get(
                uri = oAuth2Config.kakao.userInfoUri,
                responseType = KakaoUserInfoResponse::class.java,
                headers = mapOf("Authorization" to "Bearer $accessToken")
            )

            return UserProfile(
                id = response.id.toString(),
                name = response.kakaoAccount.profile?.nickname,
                email = response.kakaoAccount.email,
                picture = response.kakaoAccount.profile?.profileImageUrl,
                provider = getProviderName()
            )
        } catch (e: Exception) {
            log.error("Failed to get Kakao user profile [requestId={}]", requestId, e)
            throw ExternalServiceException(
                serviceName = getProviderName(),
                message = "카카오 사용자 프로필 요청 중 오류가 발생했습니다.",
                details = mapOf("error" to (e.message ?: "알 수 없는 오류")),
                cause = e
            )
        }
    }

    /**
     * 카카오 로그인 URL 생성
     */
    override fun generateLoginUrl(state: String): String {
        log.debug("Generating Kakao login URL with state: {}", state)

        return "${oAuth2Config.kakao.authUri}?client_id=${oAuth2Config.kakao.clientId}" +
                "&redirect_uri=${oAuth2Config.kakao.redirectUri}" +
                "&response_type=code" +
                "&state=${state}" +
                (if (oAuth2Config.kakao.scopes.isNotEmpty())
                    "&scope=${oAuth2Config.kakao.scopes}"
                else "")
    }

    /**
     * 공급자 이름 반환
     */
    override fun getProviderName(): String = "kakao"



}