package com.copago.petglam.exception.enums

import org.springframework.http.HttpStatus

enum class CommonErrorCode(
    override val code: String,
    override val defaultMessage: String,
    override val httpStatus: Int
) : ErrorCode {
    UPGRADE_REQUIRED("COM_000", "업데이트가 필요합니다.", HttpStatus.UPGRADE_REQUIRED.value()),
    INVALID_INPUT("COM_001", "유효하지 않은 입력 값 입니다.", HttpStatus.BAD_REQUEST.value()),
    RESOURCE_NOT_FOUND("COM_002", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    METHOD_NOT_ALLOWED("COM_003", "허용되지 않은 HTTP 메소드입니다.", HttpStatus.METHOD_NOT_ALLOWED.value()),
    SYSTEM_ERROR("COM_004", "시스템 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value())
}