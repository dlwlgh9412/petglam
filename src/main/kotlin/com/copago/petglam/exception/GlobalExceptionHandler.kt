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

    /**
     * 모든 커스텀 예외 (`BaseException` 상속) 처리
     */
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<ErrorResponse> {
        // 메트릭 기록
        incrementErrorMetric(ex.code)

        // 로깅 (BusinessException은 WARN 레벨, 나머지는 ERROR 레벨 등 세분화 가능)
        if (ex is BusinessException || ex is SecurityException) {
            log.warn(
                "[{}({})] {} - Details: {} (RequestId: {})",
                ex.code,
                ex.httpStatus,
                ex.message,
                ex.details,
                PetglamRequestContext.getRequestId(),
                ex // 스택 트레이스는 필요시 DEBUG
            )
        } else {
            log.error(
                "[{}({})] {} - Details: {} (RequestId: {})",
                ex.code,
                ex.httpStatus,
                ex.message,
                ex.details,
                PetglamRequestContext.getRequestId(),
                ex // Infrastructure, Base 등은 ERROR 로깅
            )
        }

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = ex.httpStatus,
            error = ex.code,
            message = ex.message,
            details = ex.details.ifEmpty { null },
            requestId = PetglamRequestContext.getRequestId()
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
        // BindException::class 는 MethodArgumentNotValidException 에서 처리됨
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

        // 유효성 검사 오류 상세 정보 추출
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
            requestId = PetglamRequestContext.getRequestId()
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
            requestId = PetglamRequestContext.getRequestId()
        )
        return ResponseEntity(errorResponse, HttpStatus.valueOf(errorCode.httpStatus))
    }


    /**
     * 존재하지 않는 API 경로 요청 처리 (404 Not Found)
     * Spring Boot 설정(spring.mvc.throw-exception-if-no-handler-found=true, spring.web.resources.add-mappings=false 필요)
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(ex: NoHandlerFoundException): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.RESOURCE_NOT_FOUND
        incrementErrorMetric(errorCode.code)
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
            requestId = PetglamRequestContext.getRequestId()
        )

        return ResponseEntity(errorResponse, HttpStatus.valueOf(errorCode.httpStatus))
    }

    /**
     * 데이터베이스 관련 예외 처리 (DataAccessException)
     */
    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(ex: DataAccessException): ResponseEntity<ErrorResponse> {
        // 데이터 무결성 위반 (예: Unique 제약 조건) -> 비즈니스 예외(중복)로 처리 가능
        if (ex is DataIntegrityViolationException) {
            // 실제로는 더 구체적인 원인(e.g., unique constraint name)을 파악하여
            // 적절한 BusinessException (e.g., UserException.alreadyExists)으로 변환하는 것이 이상적
            // 여기서는 일반적인 DB 오류로 처리하거나, 구체적인 처리가 필요하면 별도 핸들러 추가
            log.warn(
                "[{}] Data integrity violation: {} (RequestId: {})",
                InfrastructureErrorCode.DATABASE_ERROR.code,
                ex.message,
                PetglamRequestContext.getRequestId(),
                ex
            )
            // return handleBaseException(BusinessException(SpecificBusinessErrorCode.DUPLICATE_RESOURCE, cause = ex))
        } else {
            log.error(
                "[{}] Database error occurred: {} (RequestId: {})",
                InfrastructureErrorCode.DATABASE_ERROR.code,
                ex.message,
                PetglamRequestContext.getRequestId(),
                ex
            )
        }

        // DatabaseException으로 감싸서 표준 처리 위임 (메시지, 스택 트레이스 유지)
        return handleBaseException(DatabaseException(cause = ex))
    }


    /**
     * 모든 예외 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.SYSTEM_ERROR
        incrementErrorMetric(errorCode.code)

        // 예상치 못한 오류는 스택 트레이스와 함께 로깅 (항상 ERROR 레벨)
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
            // details = mapOf("exceptionType" to ex.javaClass.simpleName), // 디버깅용 상세 정보
            requestId = PetglamRequestContext.getRequestId()
        )

        return ResponseEntity(errorResponse, HttpStatus.valueOf(errorCode.httpStatus))
    }

    // 에러 메트릭 증가 함수 (기존 유지)
    private fun incrementErrorMetric(errorCode: String) {
        try {
            meterRegistry.counter("app.errors", "error.code", errorCode).increment()
        } catch (e: Exception) {
            log.warn("Failed to increment error metric for code {}: {}", errorCode, e.message)
        }
    }

    // ErrorResponse 데이터 클래스 (기존 파일에서 이동 또는 유지)
    data class ErrorResponse(
        val timestamp: LocalDateTime,
        val status: Int,
        val error: String,
        val message: String?,
        val details: Map<String, Any>? = null,
        var requestId: String
    )
}