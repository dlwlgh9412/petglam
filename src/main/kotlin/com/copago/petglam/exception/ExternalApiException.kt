package com.copago.petglam.exception

open class ExternalApiException(
    val apiUrl: String,
    message: String,
    val errorDetails: Map<String, Any> = emptyMap(),
    statusCode: Int = 500,
    cause: Throwable? = null
) : HttpException("외부 API 요청 ($apiUrl) 오류: $message", statusCode, cause)
