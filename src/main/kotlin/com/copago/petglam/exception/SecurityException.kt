package com.copago.petglam.exception

import com.copago.petglam.exception.enums.SecurityErrorCode

open class SecurityException (
    errorCode: SecurityErrorCode, // SecurityErrorCode 사용 명시
    message: String? = null,
    details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null
) : BaseException(errorCode, message, details, cause)