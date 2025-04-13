package com.copago.petglam.exception

import com.copago.petglam.context.RequestContextHolder

/**
 * OAuth2 인증 관련 예외
 * 소셜 로그인 처리 중 발생하는 모든 문제에 사용
 *
 * 예시:
 * - 지원하지 않는 소셜 로그인 제공자
 * - 인증 코드 검증 실패
 * - 사용자 프로필 정보 획득 실패
 */
class OAuth2Exception(
    val provider: String,
    message: String? = null,
    errorCode: ErrorCode = ErrorCode.AUTH_OAUTH2_ERROR,
    errorDetails: Map<String, Any> = emptyMap(),
    requestId: String? = RequestContextHolder.getRequestId(),
    cause: Throwable? = null
) : AuthenticationException(
    errorCode = errorCode,
    message = message,
    errorDetails = errorDetails + mapOf("provider" to provider),
    requestId = requestId,
    cause = cause
) {
    companion object {
        /**
         * 지원하지 않는 OAuth2 제공자에 대한 예외 생성
         */
        fun unsupportedProvider(
            provider: String,
            availableProviders: List<String>
        ): OAuth2Exception {
            return OAuth2Exception(
                provider = provider,
                message = "지원하지 않는 소셜 로그인 제공자입니다.",
                errorCode = ErrorCode.USER_OAUTH_PROVIDER_NOT_SUPPORTED,
                errorDetails = mapOf("availableProviders" to availableProviders)
            )
        }

        /**
         * 인증 코드 검증 실패에 대한 예외 생성
         */
        fun invalidAuthorizationCode(
            provider: String,
            details: Map<String, Any> = emptyMap()
        ): OAuth2Exception {
            return OAuth2Exception(
                provider = provider,
                message = "유효하지 않은 인증 코드입니다.",
                errorCode = ErrorCode.AUTH_INVALID_CREDENTIAL,
                errorDetails = details
            )
        }

        /**
         * 사용자 프로필 정보 획득 실패에 대한 예외 생성
         */
        fun userProfileFetchFailed(
            provider: String,
            errorMessage: String,
            cause: Throwable? = null
        ): OAuth2Exception {
            return OAuth2Exception(
                provider = provider,
                message = "사용자 프로필 정보를 가져오는데 실패했습니다.",
                errorCode = ErrorCode.AUTH_OAUTH2_ERROR,
                errorDetails = mapOf("error" to errorMessage),
                cause = cause
            )
        }
    }
}
