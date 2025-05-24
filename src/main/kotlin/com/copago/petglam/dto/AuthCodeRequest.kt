package com.copago.petglam.dto

data class AuthCodeRequest(
    val code: String,
    val state: String? = null,
)
