package com.copago.petglam.exception

import org.springframework.http.HttpStatus

open class AuthenticationException(
    message: String,
    val errorDetails: Map<String, Any> = emptyMap(),
) : ApplicationException("인증 오류: $message", statusCode = HttpStatus.FORBIDDEN.value())

class OAuth2Exception(
    val provider: String,
    message: String,
    errorDetails: Map<String, Any> = emptyMap(),
) : AuthenticationException(message, errorDetails + ("provider" to provider))