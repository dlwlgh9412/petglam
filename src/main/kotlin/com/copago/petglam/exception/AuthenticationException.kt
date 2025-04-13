package com.copago.petglam.exception

import com.copago.petglam.context.PetglamRequestContext

/**
 * 인증 관련 예외의 기본 클래스
 * 모든 인증 및 인가 과정에서 발생하는 예외의 부모 클래스
 *
 * 예시:
 * - 잘못된 인증 정보
 * - 만료된 토큰
 * - 권한 부족
 * - 소셜 로그인 오류
 */
open class AuthenticationException(
    errorCode: ErrorCode = ErrorCode.AUTH_INVALID_CREDENTIAL,
    message: String? = null,
    errorDetails: Map<String, Any> = emptyMap(),
    requestId: String? = PetglamRequestContext.getRequestId(),
    cause: Throwable? = null
) : HttpException(errorCode, message, errorDetails, requestId, cause) {
    companion object {
        /**
         * 유효하지 않은 인증 정보 예외
         */
        fun invalidCredentials(
            message: String? = null,
            errorDetails: Map<String, Any> = emptyMap(),
        ): AuthenticationException {
            return AuthenticationException(
                errorCode = ErrorCode.AUTH_INVALID_CREDENTIAL,
                message = message ?: "잘못된 인증 정보입니다.",
                errorDetails = errorDetails,
            )
        }

        /**
         * 만료된 토큰에 대한 예외
         */
        fun tokenExpired(
            message: String? = null,
            errorDetails: Map<String, Any> = emptyMap(),
        ): AuthenticationException {
            return AuthenticationException(
                errorCode = ErrorCode.AUTH_TOKEN_EXPIRED,
                message = message ?: "만료된 토큰입니다.",
                errorDetails = errorDetails,
            )
        }

        /**
         * 유효하지 않은 토큰에 대한 예외
         */
        fun invalidToken(
            message: String? = null,
            errorDetails: Map<String, Any> = emptyMap(),
            cause: Throwable? = null
        ): AuthenticationException {
            return AuthenticationException(
                errorCode = ErrorCode.AUTH_INVALID_TOKEN,
                message = message ?: "유효하지 않은 토큰입니다.",
                errorDetails = errorDetails,
                cause = cause
            )
        }

        /**
         * 접근 권한 부족에 대한 예외
         */
        fun accessDenied(
            message: String? = null,
            errorDetails: Map<String, Any> = emptyMap()
        ): AuthenticationException {
            return AuthenticationException(
                errorCode = ErrorCode.AUTH_ACCESS_DENIED,
                message = message ?: "접근 권한이 없습니다.",
                errorDetails = errorDetails
            )
        }
    }
}