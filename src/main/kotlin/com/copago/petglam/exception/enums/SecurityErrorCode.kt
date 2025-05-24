package com.copago.petglam.exception.enums

import org.springframework.http.HttpStatus

enum class SecurityErrorCode(
    override val code: String,
    override val defaultMessage: String,
    override val httpStatus: Int
) : ErrorCode {
    AUTHENTICATION_FAILED("SEC_AUTH_FAIL_001", "인증에 실패했습니다.", HttpStatus.UNAUTHORIZED.value()),
    TOKEN_EXPIRED("SEC_TOKEN_EXP_001", "인증 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED.value()),
    TOKEN_INVALID("SEC_TOKEN_INV_001", "유효하지 않은 인증 토큰입니다.", HttpStatus.UNAUTHORIZED.value()),
    ACCESS_DENIED("SEC_ACCESS_DENIED_001", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN.value())
}