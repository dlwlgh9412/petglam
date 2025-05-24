package com.copago.petglam.exception

import com.copago.petglam.context.PetglamRequestContext
import com.copago.petglam.exception.enums.CommonErrorCode
import com.copago.petglam.exception.enums.InfrastructureErrorCode
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler(
    private val meterRegistry: MeterRegistry
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<ErrorResponse> {
        if (ex is BusinessException || ex is SecurityException) {
            log.warn(
                "[{}({})] {} - Details: {} (RequestId: {})",
                ex.code,
                ex.httpStatus,
                ex.message,
                ex.details,
                PetglamRequestContext.getRequestId(),
                ex
            )
        } else {
            log.error(
                "[{}({})] {} - Details: {} (RequestId: {})",
                ex.code,
                ex.httpStatus,
                ex.message,
                ex.details,
                PetglamRequestContext.getRequestId(),
                ex
            )
        }

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = ex.httpStatus,
            error = ex.code,
            message = ex.message,
            details = ex.details.ifEmpty { null }
        )

        return ResponseEntity(errorResponse, HttpStatus.valueOf(ex.httpStatus))
    }

    /**
     * Spring Validation 예외 및 요청 파라미터 관련 예외 처리 (400 Bad Request)
     */
    @ExceptionHandler(
        MethodArgumentNotValidException::class,
        MissingServletRequestParameterException::class,
        MethodArgumentTypeMismatchException::class,
        HttpMessageNotReadableException::class
    )
    fun handleValidationExceptions(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.INVALID_INPUT
        incrementErrorMetric(errorCode.code)
        log.warn(
            "[{}] Validation error occurred: {} (RequestId: {})",
            errorCode.code,
            ex.message,
            PetglamRequestContext.getRequestId()
        )

        val validationErrors = when (ex) {
            is MethodArgumentNotValidException -> {
                ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
            }

            is MissingServletRequestParameterException -> {
                mapOf(ex.parameterName to "Required parameter is missing")
            }

            is MethodArgumentTypeMismatchException -> {
                mapOf(ex.name to "Invalid parameter type. Expected: ${ex.requiredType?.simpleName}")
            }

            is HttpMessageNotReadableException -> {
                mapOf("requestBody" to "Cannot parse request body or invalid format")
            }

            else -> mapOf("error" to "Invalid request data") // General case
        }


        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = errorCode.httpStatus,
            error = errorCode.code,
            message = ex.message,
            details = mapOf("validationErrors" to validationErrors),
        )

        return ResponseEntity(errorResponse, HttpStatus.valueOf(errorCode.httpStatus))
    }

    /**
     * 잘못된 HTTP 메소드 요청 처리 (405 Method Not Allowed)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.METHOD_NOT_ALLOWED
        incrementErrorMetric(errorCode.code)
        log.warn(
            "[{}] Method not allowed: {} (RequestId: {})",
            errorCode.code,
            ex.message,
            PetglamRequestContext.getRequestId()
        )

        val details = mapOf("supportedMethods" to (ex.supportedMethods?.joinToString() ?: "N/A"))

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = errorCode.httpStatus,
            error = errorCode.code,
            message = ex.message,
            details = details,
        )
        return ResponseEntity(errorResponse, HttpStatus.valueOf(errorCode.httpStatus))
    }



    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(ex: NoHandlerFoundException): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.METHOD_NOT_ALLOWED
        log.warn(
            "[{}] No handler found for {} {} (RequestId: {})",
            errorCode.code,
            ex.httpMethod,
            ex.requestURL,
            PetglamRequestContext.getRequestId()
        )


        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = errorCode.httpStatus,
            error = errorCode.code,
            message = ex.message,
        )

        return ResponseEntity(errorResponse, HttpStatus.valueOf(errorCode.httpStatus))
    }

    /**
     * 데이터베이스 관련 예외 처리 (DataAccessException)
     */
    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(ex: DataAccessException): ResponseEntity<ErrorResponse> {
        if (ex is DataIntegrityViolationException) {
            log.warn(
                "[{}] Data integrity violation: {} (RequestId: {})",
                InfrastructureErrorCode.DATABASE_ERROR.code,
                ex.message,
                PetglamRequestContext.getRequestId(),
                ex
            )

        } else {
            log.error(
                "[{}] Database error occurred: {} (RequestId: {})",
                InfrastructureErrorCode.DATABASE_ERROR.code,
                ex.message,
                PetglamRequestContext.getRequestId(),
                ex
            )
        }

        return handleBaseException(DatabaseException(cause = ex))
    }


    /**
     * 모든 예외 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.SYSTEM_ERROR
        incrementErrorMetric(errorCode.code)

        log.error(
            "[{}] Unhandled exception occurred: {} (RequestId: {})",
            errorCode.code,
            ex.message,
            PetglamRequestContext.getRequestId(),
            ex
        )


        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = errorCode.httpStatus,
            error = errorCode.code,
            message = ex.message,
        )

        return ResponseEntity(errorResponse, HttpStatus.valueOf(errorCode.httpStatus))
    }

    private fun incrementErrorMetric(errorCode: String) {
        try {
            meterRegistry.counter("app.errors", "error.code", errorCode).increment()
        } catch (e: Exception) {
            log.warn("Failed to increment error metric for code {}: {}", errorCode, e.message)
        }
    }

    data class ErrorResponse(
        val timestamp: LocalDateTime,
        val status: Int,
        val error: String,
        val message: String?,
        val details: Map<String, Any>? = null,
    )
}