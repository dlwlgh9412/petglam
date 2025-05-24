package com.copago.petglam.context

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.slf4j.MDC

import org.springframework.web.context.request.RequestContextHolder as SpringRequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID

object PetglamRequestContext {
    private const val REQUEST_ID = "requestId"

    /**
     * 현재 요청의 HttpServletRequest 반환
     * 없으면 IllegalStateException 발생
     */
    private fun getRequest(): HttpServletRequest {
        return try {
            getRequestAttributes().request
        } catch (e: Exception) {
            throw IllegalStateException("현재 요청 컨텍스트에서 HttpServletRequest를 가져올 수 없습니다.", e)
        }
    }

    /**
     * 현재 요청의 HttpServletResponse 반환
     * 없으면 IllegalStateException 발생
     */
    fun getResponse(): HttpServletResponse {
        try {
            val attributes = getRequestAttributes()
            val response = attributes.response ?: throw IllegalStateException("현재 요청에 응답이 없습니다")
            return response
        } catch (e: Exception) {
            throw IllegalStateException("현재 요청 컨텍스트에서 HttpServletResponse를 가져올 수 없습니다.", e)
        }
    }

    /**
     * 현재 요청의 HttpSession 반환
     * 없으면 새로 생성하여 반환
     */
    fun getSession(): HttpSession {
        return try {
            getRequest().session
        } catch (e: Exception) {
            throw IllegalStateException("현재 요청 컨텍스트에서 HttpSession을 가져올 수 없습니다.", e)
        }
    }

    private fun getRequestAttributes(): ServletRequestAttributes {
        val attributes = SpringRequestContextHolder.getRequestAttributes()
            ?: throw IllegalStateException("No current ServletRequestAttributes")

        if (attributes is ServletRequestAttributes) {
            return attributes
        } else {
            throw IllegalStateException("Current request attributes are not ServletRequestAttributes")
        }
    }

    fun getRequestId(): String {
        return MDC.get(REQUEST_ID) ?: generateRequestId().also {
            MDC.put(REQUEST_ID, it)
        }
    }

    /**
     * 요청 ID 설정
     */
    fun setRequestId(requestId: String) {
        MDC.put(REQUEST_ID, requestId)
    }

    /**
     * 새로운 요청 ID 생성
     */
    private fun generateRequestId(): String {
        return UUID.randomUUID().toString()
    }

    fun clear() {
        MDC.remove(REQUEST_ID)
    }
}