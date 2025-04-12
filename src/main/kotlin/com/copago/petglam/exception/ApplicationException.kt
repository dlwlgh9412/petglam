package com.copago.petglam.exception

open class ApplicationException(
    message: String,
    val statusCode: Int,
) : RuntimeException(message)