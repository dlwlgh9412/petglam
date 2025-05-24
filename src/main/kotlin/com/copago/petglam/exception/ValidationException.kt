package com.copago.petglam.exception

import com.copago.petglam.exception.enums.CommonErrorCode

open class ValidationException(
    message: String,
    details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null
) : BusinessException(CommonErrorCode.INVALID_INPUT, message, details, cause) {

}