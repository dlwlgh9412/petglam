package com.copago.petglam.filter

import com.copago.petglam.context.PetglamRequestContext
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Order(3)
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
            val requestId = request.getHeader(X_REQUEST_ID) ?: PetglamRequestContext.getRequestId()
            PetglamRequestContext.setRequestId(requestId)

            response.addHeader(X_REQUEST_ID, requestId)

            filterChain.doFilter(request, response)
        } finally {
            PetglamRequestContext.clear()
        }
    }
}