package com.copago.petglam.exception.enums

interface ErrorCode {
    val code: String
    val defaultMessage: String
    val httpStatus: Int
}