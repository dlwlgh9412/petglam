package com.copago.petglam.exception

enum class ErrorCode(
    val code: String,
    val defaultMessage: String,
    val httpStatus: Int
) {
    // 공통 오류 (COMMON)
    COMMON_SYSTEM_ERROR("COMMON-SYS-001", "시스템 오류가 발생했습니다.", 500),
    COMMON_INVALID_PARAMETER("COMMON-VAL-001", "유효하지 않은 파라미터입니다.", 400),
    COMMON_RESOURCE_NOT_FOUND("COMMON-RES-001", "요청한 리소스를 찾을 수 없습니다.", 404),

    // 인증 관련 오류 (AUTH)
    AUTH_INVALID_TOKEN("AUTH-TOK-001", "유효하지 않은 토큰입니다.", 401),
    AUTH_TOKEN_EXPIRED("AUTH-TOK-002", "만료된 토큰입니다.", 401),
    AUTH_ACCESS_DENIED("AUTH-PRM-001", "접근 권한이 없습니다.", 403),
    AUTH_OAUTH2_ERROR("AUTH-OAUTH-001", "소셜 로그인 처리 중 오류가 발생했습니다.", 400),
    AUTH_INVALID_CREDENTIAL("AUTH-CRD-001", "잘못된 인증 정보입니다.", 401),

    // 외부 API 통신 오류 (API)
    API_COMMUNICATION_ERROR("API-COM-001", "외부 API 통신 중 오류가 발생했습니다.", 500),
    API_TIMEOUT("API-COM-002", "외부 API 요청 시간이 초과되었습니다.", 504),
    API_CLIENT_ERROR("API-COM-003", "외부 API 클라이언트 오류가 발생했습니다.", 400),
    API_SERVER_ERROR("API-COM-004", "외부 API 서버 오류가 발생했습니다.", 502),

    // 사용자 관련 오류 (USER)
    USER_NOT_FOUND("USER-INF-001", "사용자를 찾을 수 없습니다.", 404),
    USER_ALREADY_EXISTS("USER-REG-001", "이미 등록된 사용자입니다.", 409),
    USER_INVALID_EMAIL("USER-VAL-001", "유효하지 않은 이메일 형식입니다.", 400),
    USER_OAUTH_PROVIDER_NOT_SUPPORTED("USER-OAUTH-001", "지원하지 않는 소셜 로그인 제공자입니다.", 400);

    companion object {
        fun fromCode(code: String): ErrorCode {
            return values().find { it.code == code } ?: COMMON_SYSTEM_ERROR
        }
    }
}