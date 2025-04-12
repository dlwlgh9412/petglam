package com.copago.petglam.exception

open class HttpException(
    message: String,
    statusCode: Int,
) : ApplicationException(message, statusCode)

class BadRequestException(message: String) :
    HttpException(message, 400)

class UnauthorizedException(message: String) :
    HttpException(message, 401)

class ForbiddenException(message: String) :
    HttpException(message, 403)

class ResourceNotFoundException(message: String) :
    HttpException(message, 404)

class ServerErrorException(message: String) :
    HttpException(message, 500)