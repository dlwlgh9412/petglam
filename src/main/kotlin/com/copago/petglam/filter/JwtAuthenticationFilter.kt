package com.copago.petglam.filter

import com.copago.petglam.exception.UnauthorizedException
import com.copago.petglam.service.JwtService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 인증이 필요 없는 경로는 필터를 건너뜀
        if (shouldSkipFilter(request)) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            // Authorization 헤더에서 토큰 추출
            val token = extractToken(request)
                ?: throw UnauthorizedException("인증 토큰이 필요합니다.")

            // 토큰 검증
            val claims = jwtService.validateToken(token)

            // 사용자 ID와 역할을 요청 속성에 설정
            request.setAttribute("userId", jwtService.getUserIdFromToken(token))
            request.setAttribute("userRoles", jwtService.getRolesFromToken(token))

            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            // 인증 오류 응답 처리
            sendErrorResponse(response, e)
        }
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    private fun shouldSkipFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/api/v1/oauth2") ||
                path.startsWith("/h2-console") ||
                path.startsWith("/error") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs")
    }

    private fun sendErrorResponse(response: HttpServletResponse, exception: Exception) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to HttpStatus.UNAUTHORIZED.value(),
            "error" to HttpStatus.UNAUTHORIZED.reasonPhrase,
            "message" to (exception.message ?: "인증에 실패했습니다.")
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
