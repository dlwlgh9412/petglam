package com.copago.petglam.exception.enums

import org.springframework.http.HttpStatus

enum class OAuth2ErrorCode(
    override val code: String,
    override val defaultMessage: String,
    override val httpStatus: Int
) : ErrorCode {
    UNSUPPORTED_PROVIDER("OAUTH_UNSUPPORTED_001", "지원하지 않는 소셜 로그인 제공자입니다.", HttpStatus.BAD_REQUEST.value())
}