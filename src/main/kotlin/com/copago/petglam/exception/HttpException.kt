package com.copago.petglam.exception

open class HttpException(
    errorCode: ErrorCode,
    message: String? = null,
    errorDetails: Map<String, Any> = emptyMap(),
    requestId: String? = null,
    cause: Throwable? = null,
) : ApplicationException(errorCode, message, errorDetails, requestId, cause)

/**
 * 400 Bad Request 예외
 * 클라이언트 요청에 문제가 있을 때 사용
 */
class BadRequestException(
    message: String? = null,
    errorCode: ErrorCode = ErrorCode.COMMON_INVALID_PARAMETER,
    errorDetails: Map<String, Any> = emptyMap(),
    requestId: String? = null
) : HttpException(errorCode, message, errorDetails, requestId)

/**
 * 401 Unauthorized 예외
 * 인증이 필요하거나 인증에 실패했을 때 사용
 */
class UnauthorizedException(
    message: String? = null,
    errorCode: ErrorCode = ErrorCode.AUTH_INVALID_TOKEN,
    errorDetails: Map<String, Any> = emptyMap(),
    requestId: String? = null
) : HttpException(errorCode, message, errorDetails, requestId)

/**
 * 403 Forbidden 예외
 * 권한이 없을 때 사용
 */
class ForbiddenException(
    message: String? = null,
    errorCode: ErrorCode = ErrorCode.AUTH_ACCESS_DENIED,
    errorDetails: Map<String, Any> = emptyMap(),
    requestId: String? = null
) : HttpException(errorCode, message, errorDetails, requestId)

/**
 * 404 Not Found 예외
 * 요청한 리소스를 찾을 수 없을 때 사용
 */
class ResourceNotFoundException(
    message: String? = null,
    errorCode: ErrorCode = ErrorCode.COMMON_RESOURCE_NOT_FOUND,
    errorDetails: Map<String, Any> = emptyMap(),
    requestId: String? = null
) : HttpException(errorCode, message, errorDetails, requestId)

/**
 * 500 Internal Server Error 예외
 * 서버 내부 오류가 발생했을 때 사용
 */
class ServerErrorException(
    message: String? = null,
    errorCode: ErrorCode = ErrorCode.COMMON_SYSTEM_ERROR,
    errorDetails: Map<String, Any> = emptyMap(),
    requestId: String? = null,
    cause: Throwable? = null
) : HttpException(errorCode, message, errorDetails, requestId, cause)