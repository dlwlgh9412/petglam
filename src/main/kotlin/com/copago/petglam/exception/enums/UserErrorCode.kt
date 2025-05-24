package com.copago.petglam.exception.enums

import org.springframework.http.HttpStatus

enum class UserErrorCode(
    override val code: String,
    override val defaultMessage: String,
    override val httpStatus: Int
) : ErrorCode {
    NOT_FOUND("USER_NF_001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    ALREADY_EXISTS("USER_AE_001", "이미 등록된 사용자입니다.", HttpStatus.CONFLICT.value()),
    INVALID_EMAIL("USER_INV_EMAIL_001", "유효하지 않은 이메일 형식입니다.", HttpStatus.BAD_REQUEST.value()),
    PASSWORD_MISMATCH("USER_PW_MISMATCH_001", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED.value())
}
