package com.copago.petglam.exception

import com.copago.petglam.context.PetglamRequestContext
import com.copago.petglam.service.ErrorMessageService
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.RestClientException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler(
    private val errorMessageService: ErrorMessageService,
    private val meterRegistry: MeterRegistry
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ApplicationException::class)
    fun handleApiException(ex: ApplicationException): ResponseEntity<ErrorResponse> {
        incrementErrorMetric(ex.errorCodeString)

        val message = errorMessageService.getMessage(ex.errorCodeString)

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = ex.statusCode,
            error = ex.errorCodeString,
            message = message,
            details = ex.errorDetails,
            requestId = ex.requestId ?: PetglamRequestContext.getRequestId()
        )

        return ResponseEntity(errorResponse, HttpStatus.valueOf(ex.statusCode))
    }

    /**
     * 유효성 검사 예외 처리
     */
    @ExceptionHandler(
        MethodArgumentNotValidException::class,
        BindException::class,
        MissingServletRequestParameterException::class,
        MethodArgumentTypeMismatchException::class,
        HttpMessageNotReadableException::class
    )
    fun handleValidationExceptions(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.COMMON_INVALID_PARAMETER
        incrementErrorMetric(errorCode.code)

        // 유효성 검사 오류 상세 정보 추출
        val validationErrors = when (ex) {
            is MethodArgumentNotValidException -> {
                ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "유효하지 않은 값") }
            }
            is BindException -> {
                ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "유효하지 않은 값") }
            }
            is MissingServletRequestParameterException -> {
                mapOf(ex.parameterName to "필수 파라미터가 누락되었습니다.")
            }
            is MethodArgumentTypeMismatchException -> {
                mapOf(ex.name to "잘못된 타입의 값이 입력되었습니다.")
            }
            else -> {
                mapOf("body" to "요청 본문을 파싱할 수 없습니다.")
            }
        }

        val message = errorMessageService.getMessage(errorCode.code)

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = errorCode.code,
            message = message,
            details = mapOf("validationErrors" to validationErrors),
            requestId = PetglamRequestContext.getRequestId()
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * 요청한 리소스를 찾을 수 없는 예외 처리
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.COMMON_RESOURCE_NOT_FOUND
        incrementErrorMetric(errorCode.code)

        val message = errorMessageService.getMessage(errorCode.code)

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = errorCode.code,
            message = message,
            requestId = PetglamRequestContext.getRequestId()
        )

        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    /**
     * 외부 API 통신 오류 처리
     */
    @ExceptionHandler(RestClientException::class)
    fun handleRestClientException(ex: RestClientException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.API_COMMUNICATION_ERROR
        incrementErrorMetric(errorCode.code)

        log.warn("External API error: {}", ex.message)

        val message = errorMessageService.getMessage(errorCode.code)

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.SERVICE_UNAVAILABLE.value(),
            error = errorCode.code,
            message = message,
            requestId = PetglamRequestContext.getRequestId()
        )

        return ResponseEntity(errorResponse, HttpStatus.SERVICE_UNAVAILABLE)
    }

    /**
     * 모든 예외를 처리하는 기본 핸들러
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.COMMON_SYSTEM_ERROR
        incrementErrorMetric(errorCode.code)

        // 예상치 못한 오류는 스택 트레이스와 함께 로깅
        log.error("Unhandled exception", ex)

        val message = errorMessageService.getMessage(errorCode.code)

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = errorCode.code,
            message = message,
            requestId = PetglamRequestContext.getRequestId()
        )

        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun incrementErrorMetric(errorCode: String) {
        meterRegistry.counter("app.errors", "error.code", errorCode).increment()
    }
}

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val details: Map<String, Any>? = null,
    var requestId: String
)