package com.copago.petglam.context

import org.slf4j.MDC
import java.util.UUID

object RequestContextHolder {
    private const val REQUEST_ID = "requestId"
    private const val LOCALE = "locale"

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
        return MDC.get(LOCALE) ?: "ko"
    }

    /**
     * 언어 설정
     */
    fun setLocale(locale: String) {
        MDC.put(LOCALE, locale)
    }

    /**
     * 컨텍스트 정보 초기화
     */
    fun clear() {
        MDC.remove(REQUEST_ID)
        MDC.remove(LOCALE)
    }
}