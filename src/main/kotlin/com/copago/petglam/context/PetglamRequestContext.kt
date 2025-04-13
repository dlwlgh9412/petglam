package com.copago.petglam.context

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.slf4j.MDC
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.RequestContextHolder as SpringRequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID

/**
 * Spring의 RequestContextHolder와 커스텀 RequestContextHolder의 기능을 합친 유틸리티 클래스
 *
 * 1. Spring의 RequestContextHolder 기능: 현재 요청에 대한 HttpServletRequest, HttpSession 등의 접근
 * 2. 커스텀 RequestContextHolder 기능: 요청 ID 및 로케일 관리
 */
object PetglamRequestContext {
    // MDC 키 상수
    private const val REQUEST_ID = "requestId"
    private const val LOCALE = "locale"

    // 기본 로케일 설정
    private const val DEFAULT_LOCALE = "ko"
    private val SUPPORTED_LOCALES = listOf("ko", "en")

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

    /**
     * 현재 요청의 ServletRequestAttributes 반환
     * 없으면 IllegalStateException 발생
     */
    private fun getRequestAttributes(): ServletRequestAttributes {
        val attributes = SpringRequestContextHolder.getRequestAttributes()
            ?: throw IllegalStateException("No current ServletRequestAttributes")

        if (attributes is ServletRequestAttributes) {
            return attributes
        } else {
            throw IllegalStateException("Current request attributes are not ServletRequestAttributes")
        }
    }

    /**
     * 요청 속성 값 가져오기
     */
    fun getAttribute(name: String, scope: Int = RequestAttributes.SCOPE_REQUEST): Any {
        val attributes = try {
            SpringRequestContextHolder.currentRequestAttributes()
        } catch (e: IllegalStateException) {
            throw IllegalStateException("현재 요청 컨텍스트가 없습니다.", e)
        }

        val attribute = attributes.getAttribute(name, scope)
        return attribute ?: throw IllegalStateException("속성 ${name}을 찾을 수 없습니다.")
    }

    /**
     * 요청 속성 값 가져오기 (null 허용)
     */
    fun getAttributeOrNull(name: String, scope: Int = RequestAttributes.SCOPE_REQUEST): Any? {
        val attributes = try {
            SpringRequestContextHolder.currentRequestAttributes()
        } catch (e: IllegalStateException) {
            return null
        }

        return attributes.getAttribute(name, scope)
    }

    /**
     * 요청 속성 값 설정하기
     */
    fun setAttribute(name: String, value: Any?, scope: Int = RequestAttributes.SCOPE_REQUEST) {
        val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes ?: return

        if (value != null) {
            attributes.setAttribute(name, value, scope)
        }
    }

    /**
     * 현재 요청의 ID를 반환
     * 없으면 새로 생성
     */
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

    /**
     * 현재 요청의 언어 설정 반환
     * 기본값은 ko (한국어)
     */
    fun getLocale(): String {
        return MDC.get(LOCALE) ?: DEFAULT_LOCALE
    }

    /**
     * 언어 설정
     */
    fun setLocale(locale: String) {
        MDC.put(LOCALE, locale)
    }

    /**
     * 요청에서 Accept-Language 헤더를 파싱하여 지원하는 로케일 반환
     */
    fun parseLocaleFromRequest(request: HttpServletRequest): String {
        val acceptLanguage = request.getHeader("Accept-Language")
        return parseLocale(acceptLanguage)
    }

    /**
     * Accept-Language 값 파싱
     */
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

    /**
     * MDC 컨텍스트 정보 초기화
     * 요청 처리 완료 후 항상 호출해야 함
     */
    fun clear() {
        MDC.remove(REQUEST_ID)
        MDC.remove(LOCALE)
    }
}