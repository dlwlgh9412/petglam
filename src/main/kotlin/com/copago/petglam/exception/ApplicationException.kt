package com.copago.petglam.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class ApplicationException(
    val errorCode: ErrorCode,
    message: String? = null,
    val errorDetails: Map<String, Any> = emptyMap(),
    val requestId: String? = null,
    cause: Throwable? = null
) : RuntimeException(message ?: errorCode.defaultMessage, cause) {
    val statusCode: Int = errorCode.httpStatus
    val errorCodeString: String = errorCode.code

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicationException::class.java)
    }

    init {
        val maskedDetails = maskSensitiveInfo(errorDetails)
        log.error(
            "ApplicationException: code=[{}], message=[{}], details=[{}], requestId=[{}]",
            errorCodeString, message ?: errorCode.defaultMessage, maskedDetails, requestId, cause
        )
    }

    private fun maskSensitiveInfo(details: Map<String, Any>): Map<String, Any> {
        val sensitiveKeys = setOf("password", "token", "secret", "credential", "accessToken", "refreshToken")
        return details.mapValues { (key, value) ->
            if (sensitiveKeys.any { key.contains(it, ignoreCase = true) }) {
                if (value is String && value.isNotEmpty()) {
                    "********"
                } else {
                    value
                }
            } else {
                value
            }
        }
    }
}