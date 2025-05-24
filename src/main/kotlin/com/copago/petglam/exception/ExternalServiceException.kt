package com.copago.petglam.exception

import com.copago.petglam.exception.enums.InfrastructureErrorCode

class ExternalServiceException (
    val serviceName: String,
    message: String? = null,
    val isRetryable: Boolean = false, // 재시도 가능 여부
    details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null,
    errorCode: InfrastructureErrorCode = InfrastructureErrorCode.EXTERNAL_SERVICE_ERROR
) : BaseException(
    errorCode = errorCode,
    message = message ?: "$serviceName 서비스 연동 중 오류 발생",
    details = details + mapOf("serviceName" to serviceName, "isRetryable" to isRetryable),
    cause = cause
)