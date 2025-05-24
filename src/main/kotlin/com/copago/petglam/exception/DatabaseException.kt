package com.copago.petglam.exception

import com.copago.petglam.exception.enums.InfrastructureErrorCode
import org.springframework.dao.DataAccessException

class DatabaseException(
    message: String? = null,
    cause: DataAccessException
) : BaseException(
    errorCode = InfrastructureErrorCode.DATABASE_ERROR,
    message = message ?: InfrastructureErrorCode.DATABASE_ERROR.defaultMessage,
    cause = cause
)