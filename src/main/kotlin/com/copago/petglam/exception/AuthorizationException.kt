package com.copago.petglam.exception

import com.copago.petglam.exception.enums.SecurityErrorCode

class AuthorizationException(
    message: String? = null,
    details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null
) : SecurityException(SecurityErrorCode.ACCESS_DENIED, message, details, cause)