package com.copago.petglam.filter

import com.copago.petglam.context.PetglamRequestContext
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 요청 컨텍스트 정보를 설정하고 관리하는 필터
 * Spring의 RequestContextFilter와 이름이 겹치지 않도록 이름 변경
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class PetglamRequestContextFilter : OncePerRequestFilter() {
    companion object {
        const val X_REQUEST_ID = "X-Request-Id"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // 요청 ID 설정 (헤더에서 가져오거나 새로 생성)
            val requestId = request.getHeader(X_REQUEST_ID) ?: PetglamRequestContext.getRequestId()
            PetglamRequestContext.setRequestId(requestId)

            // 응답 헤더에 요청 ID 추가
            response.addHeader(X_REQUEST_ID, requestId)

            // 로케일 설정
            val locale = PetglamRequestContext.parseLocaleFromRequest(request)
            PetglamRequestContext.setLocale(locale)

            // 필터 체인 계속 실행
            filterChain.doFilter(request, response)
        } finally {
            // 요청 처리 완료 후 컨텍스트 정보 초기화
            PetglamRequestContext.clear()
        }
    }
}