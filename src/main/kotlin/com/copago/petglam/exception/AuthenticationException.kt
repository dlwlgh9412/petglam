package com.copago.petglam.exception

import com.copago.petglam.exception.enums.SecurityErrorCode

class AuthenticationException(
    errorCode: SecurityErrorCode,
    message: String? = null,
    details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null
) : SecurityException(errorCode, message, details, cause) {
    companion object {
        fun failed(
            message: String? = null,
            details: Map<String, Any> = emptyMap(),
            cause: Throwable? = null
        ): AuthenticationException {
            return AuthenticationException(SecurityErrorCode.AUTHENTICATION_FAILED, message, details, cause)
        }

        fun tokenExpired(details: Map<String, Any> = emptyMap()): AuthenticationException {
            return AuthenticationException(SecurityErrorCode.TOKEN_EXPIRED, details = details)
        }

        fun tokenInvalid(
            message: String? = null,
            details: Map<String, Any> = emptyMap(),
            cause: Throwable? = null
        ): AuthenticationException {
            return AuthenticationException(SecurityErrorCode.TOKEN_INVALID, message, details, cause)
        }
    }
}