package com.copago.petglam.filter

import com.copago.petglam.config.JwtConfig
import com.copago.petglam.context.PetglamRequestContext
import com.copago.petglam.exception.AuthenticationException
import com.copago.petglam.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order

import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Order(2)
@Component
class JwtAuthenticationFilter(
    private val jwtConfig: JwtConfig,
    private val jwtService: JwtService,
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
            val token = extractToken(request) ?: throw AuthenticationException.tokenInvalid(message = "인증 토큰이 필요합니다.")
            log.debug("Validating JWT token for path: {} [requestId={}]", request.requestURI, requestId)
            jwtService.validateToken(token)

            request.setAttribute("userId", jwtService.getUserIdFromToken(token))
            request.setAttribute("userRoles", jwtService.getRolesFromToken(token))

            filterChain.doFilter(request, response)
        } catch (e: AuthenticationException) {
            throw e
        } catch (e: Exception) {
            // 인증 오류 응답 처리
            log.warn(
                "JWT authentication failed for path: {} [requestId={}]",
                request.requestURI, requestId, e
            )
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
        return jwtConfig.excludePaths.any { pattern -> pathMapper.match(pattern, path) }
    }
}