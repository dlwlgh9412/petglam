package com.copago.petglam.exception

class ApiException(
    message: String,
    statusCode: Int,
    val errorCode: String? = null
) : HttpException(message, statusCode)
