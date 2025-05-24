package com.copago.petglam.exception

import com.copago.petglam.exception.enums.CommonErrorCode
import com.copago.petglam.exception.enums.ErrorCode

open class BaseException(
    val errorCode: ErrorCode,
    message: String? = null,
    val details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null
) : RuntimeException(message ?: errorCode.defaultMessage, cause) {
    val httpStatus: Int = errorCode.httpStatus
    val code: String = errorCode.code

    companion object {
        fun upgradeRequired(clientVersion: String, minVersion: String): BaseException {
            return BaseException(
                errorCode = CommonErrorCode.UPGRADE_REQUIRED,
                message = CommonErrorCode.UPGRADE_REQUIRED.defaultMessage + " 최소버전은 $minVersion 입니다.",
                details = mapOf(
                    "clientVersion" to clientVersion,
                    "minVersion" to minVersion
                ),
            )
        }
    }
}