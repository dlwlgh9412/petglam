package com.copago.petglam.exception.enums

import org.springframework.http.HttpStatus

enum class InfrastructureErrorCode(
    override val code: String,
    override val defaultMessage: String,
    override val httpStatus: Int
) : ErrorCode {
    EXTERNAL_SERVICE_ERROR("INF_EXT_SVC_ERR_001", "외부 서비스 연동 중 오류가 발생했습니다.", HttpStatus.SERVICE_UNAVAILABLE.value()),
    DATABASE_ERROR("INF_DB_ERR_001", "데이터베이스 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    CONFIG_ERROR("INF_CFG_ERR_001", "시스템 설정 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    TIMEOUT("INF_TIMEOUT_001", "처리 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT.value())
}