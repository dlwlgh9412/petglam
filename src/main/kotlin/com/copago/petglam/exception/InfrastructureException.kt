package com.copago.petglam.exception

import com.copago.petglam.exception.enums.InfrastructureErrorCode

open class InfrastructureException (
    errorCode: InfrastructureErrorCode,
    message: String? = null,
    details: Map<String, Any> = emptyMap(),
    val isRetryable: Boolean = false, // 재시도 가능 여부
    cause: Throwable? = null
) : BaseException(errorCode, message, details, cause)