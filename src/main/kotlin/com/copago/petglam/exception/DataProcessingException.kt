package com.copago.petglam.exception

class DataProcessingException(
    message: String,
    val operation: String,
    val entity: String,
    cause: Throwable? = null
) : ApplicationException("데이터 처리 오류($operation, $entity): $message", cause)