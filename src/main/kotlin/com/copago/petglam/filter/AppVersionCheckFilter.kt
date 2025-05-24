package com.copago.petglam.filter

import com.copago.petglam.exception.BaseException
import com.copago.petglam.service.AppVersionService
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(1)
class AppVersionCheckFilter(
    private val appVersionService: AppVersionService,
) : Filter {
    private val log = LoggerFactory.getLogger(javaClass)
    private val appVersionHeader = "X-App-Version"
    private val userAgentHeader = "User-Agent"
    private val osTypeHeader = "X-OS-Type"

    override fun doFilter(
        request: ServletRequest, response: ServletResponse, chain: FilterChain
    ) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        if (httpRequest.requestURI.startsWith("/actuator")) {
            chain.doFilter(request, response)
            return
        }

        val clientVersion = httpRequest.getHeader(appVersionHeader);
        val userAgent = httpRequest.getHeader(userAgentHeader);
        val osTypeHeader = httpRequest.getHeader(osTypeHeader);

        try {
            appVersionService.checkSupport(clientVersion, userAgent, osTypeHeader)
            chain.doFilter(request, response)
        } catch (e: BaseException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }
}