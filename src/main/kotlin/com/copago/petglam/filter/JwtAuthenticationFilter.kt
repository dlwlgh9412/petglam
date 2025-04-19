package com.copago.petglam.filter

import com.copago.petglam.config.JwtConfig
import com.copago.petglam.context.PetglamRequestContext
import com.copago.petglam.exception.AuthenticationException
import com.copago.petglam.exception.ErrorResponse
import com.copago.petglam.service.ErrorMessageService
import com.copago.petglam.service.JwtService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime

@Component
class JwtAuthenticationFilter(
    private val jwtConfig: JwtConfig,
    private val jwtService: JwtService,
    private val objectMapper: ObjectMapper,
    private val errorMessageService: ErrorMessageService,
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    private val pathMapper = AntPathMatcher()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestId = PetglamRequestContext.getRequestId()

        // 인증이 필요 없는 경로는 필터를 건너뜀
        if (shouldSkipFilter(request)) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            // Authorization 헤더에서 토큰 추출
            val token = extractToken(request)
                ?: throw AuthenticationException.invalidCredentials(
                    message = "인증 토큰이 필요합니다.",
                    errorDetails = mapOf("path" to request.requestURI)
                )

            log.debug("Validating JWT token for path: {} [requestId={}]", request.requestURI, requestId)

            // 토큰 검증 (예외는 JwtService에서 처리)
            jwtService.validateToken(token)

            // 사용자 ID와 역할을 요청 속성에 설정
            request.setAttribute("userId", jwtService.getUserIdFromToken(token))
            request.setAttribute("userRoles", jwtService.getRolesFromToken(token))

            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            // 인증 오류 응답 처리
            log.warn(
                "JWT authentication failed for path: {} [requestId={}]",
                request.requestURI, requestId, e
            )
            sendErrorResponse(response, e)
        }
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    /**
     * 인증 필터를 건너뛸 경로 확인
     */
    private fun shouldSkipFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return jwtConfig.excludePaths.any { pattern -> pathMapper.match(pattern, path)}
    }

    /**
     * 인증 오류 응답 전송
     */
    private fun sendErrorResponse(response: HttpServletResponse, exception: Exception) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        // 오류를 AuthenticationException으로 변환
        val authException = when (exception) {
            is AuthenticationException -> exception
            else -> AuthenticationException.invalidCredentials(
                message = exception.message,
                errorDetails = mapOf("originalError" to (exception.message ?: "알 수 없는 오류"))
            )
        }

        // 다국어 오류 메시지 조회
        val message = errorMessageService.getMessage(authException.errorCodeString)

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.UNAUTHORIZED.value(),
            error = authException.errorCodeString,
            message = message,
            details = authException.errorDetails,
            requestId = authException.requestId ?: PetglamRequestContext.getRequestId()
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}