package com.copago.petglam.exception

import com.copago.petglam.exception.enums.OAuth2ErrorCode

class OAuth2Exception(
    errorCode: OAuth2ErrorCode,
    message: String? = null,
    details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null
) : BaseException(errorCode, message, details, cause) {
    companion object {
        fun unsupportedProvider(provider: String, availableProviders: List<String>): OAuth2Exception {
            return OAuth2Exception(
                errorCode = OAuth2ErrorCode.UNSUPPORTED_PROVIDER,
                details = mapOf("requestedProvider" to provider, "availableProviders" to availableProviders)
            )
        }
    }
}