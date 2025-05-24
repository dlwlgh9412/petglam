package com.copago.petglam.exception

import com.copago.petglam.exception.enums.UserErrorCode

class UserException(
    errorCode: UserErrorCode, // UserErrorCode 사용 명시
    message: String? = null,
    details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null
) : BaseException(errorCode, message, details, cause) {
    companion object {
        fun notFound(userId: String? = null, email: String? = null): UserException {
            val details = mutableMapOf<String, Any>()
            userId?.let { details["userId"] = it }
            email?.let { details["email"] = it }
            return UserException(
                errorCode = UserErrorCode.NOT_FOUND,
                details = details
            )
        }

        fun alreadyExists(email: String): UserException {
            return UserException(
                errorCode = UserErrorCode.ALREADY_EXISTS,
                details = mapOf("email" to email)
            )
        }

        fun invalidEmail(email: String?): UserException {
            return UserException(
                errorCode = UserErrorCode.INVALID_EMAIL,
                details = mapOf("email" to (email ?: "N/A"))
            )
        }

        fun passwordMismatch(): UserException {
            return UserException(errorCode = UserErrorCode.PASSWORD_MISMATCH)
        }
    }
}