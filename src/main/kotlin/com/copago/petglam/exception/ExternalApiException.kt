package com.copago.petglam.exception

import org.springframework.http.HttpStatus

open class ExternalApiException(
    val uri: String,
    message: String? = null,
    errorDetails: Map<String, Any> = emptyMap(),
    errorCode: ErrorCode = determineErrorCode(errorDetails["statusCode"] as? Int),
    requestId: String? = null,
    cause: Throwable? = null
) : HttpException(errorCode, message, errorDetails + ("uri" to uri), requestId, cause) {

    companion object {
        /**
         * HTTP 상태 코드에 따라 적절한 오류 코드 결정
         */
        private fun determineErrorCode(statusCode: Int?): ErrorCode {
            return when {
                statusCode == null -> ErrorCode.API_COMMUNICATION_ERROR
                statusCode == HttpStatus.GATEWAY_TIMEOUT.value() -> ErrorCode.API_TIMEOUT
                statusCode in 400..499 -> ErrorCode.API_CLIENT_ERROR
                statusCode in 500..599 -> ErrorCode.API_SERVER_ERROR
                else -> ErrorCode.API_COMMUNICATION_ERROR
            }
        }
    }

    /**
     * 재시도 가능한 오류인지 확인
     */
    fun isRetryable(): Boolean {
        val statusCode = errorDetails["statusCode"] as? Int ?: return false

        // 일시적인 서버 오류나 타임아웃은 재시도 가능
        return statusCode == 429 || // Too Many Requests
                statusCode == 503 || // Service Unavailable
                statusCode == 504    // Gateway Timeout
    }
}
