package com.copago.petglam.exception

import com.copago.petglam.exception.enums.ErrorCode


open class BusinessException(
    errorCode: ErrorCode,
    message: String? = null,
    details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null
) : BaseException(errorCode, message, details, cause)