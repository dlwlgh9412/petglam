package com.copago.petglam.exception

import com.copago.petglam.exception.enums.InfrastructureErrorCode
import org.springframework.http.HttpMethod
import java.net.URI

class HttpClientException(
    val method: HttpMethod,
    val uri: URI,
    val statusCode: Int? = null, // 상태 코드가 없을 수도 있음 (e.g., 연결 실패)
    val rawBody: String? = null, // 오류 응답 본문 (디버깅용)
    message: String, // 오류에 대한 설명
    val requestId: String? = null,
    cause: Throwable? = null
) : InfrastructureException(
    errorCode = InfrastructureErrorCode.EXTERNAL_SERVICE_ERROR,
    message = message,
    details = mapOf(
        "method" to method.name(),
        "uri" to uri.toString(),
        "statusCode" to (statusCode?.toString() ?: "N/A"),
        "requestId" to (requestId ?: "N/A")
    ).filterValues { it != "N/A" },
    cause = cause
)