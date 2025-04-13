package com.copago.petglam.exception

open class ExternalApiException(
    uri: String,
    message: String,
    errorDetails: Map<String, Any>,
    statusCode: Int = 500,
    cause: Throwable? = null
) : HttpException("외부 API 요청 ($uri) 오류: $message", statusCode)
