package com.copago.petglam.filter

import com.copago.petglam.context.RequestContextHolder
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class RequestContextFilter : OncePerRequestFilter() {
    companion object {
        const val X_REQUEST_ID = "X_REQUEST_ID"
        const val DEFAULT_LOCALE = "ko"
        val SUPPORTED_LOCALES = listOf("ko")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val requestId = request.getHeader(X_REQUEST_ID) ?: RequestContextHolder.getRequestId();
            RequestContextHolder.setRequestId(requestId)

            // 응답 헤더에 요청 ID 추가
            response.addHeader(X_REQUEST_ID, requestId)

            val acceptLanguage = request.getHeader("Accept-Language")
            val locale = parseLocale(acceptLanguage)
            RequestContextHolder.setLocale(locale)

            filterChain.doFilter(request, response)
        } finally {
            RequestContextHolder.clear()
        }
    }

    private fun parseLocale(acceptLanguage: String?): String {
        if (acceptLanguage.isNullOrBlank()) {
            return DEFAULT_LOCALE
        }

        // 첫 번째 언어 코드 추출 (예: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7 -> ko)
        val locale = acceptLanguage.split(',').firstOrNull()
            ?.split(';')?.firstOrNull()
            ?.split('-')?.firstOrNull()
            ?.lowercase()

        return when {
            locale.isNullOrBlank() -> DEFAULT_LOCALE
            SUPPORTED_LOCALES.contains(locale) -> locale
            else -> DEFAULT_LOCALE
        }
    }

}