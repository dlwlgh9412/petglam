package com.copago.petglam.model

data class AuthCodeRequest(
    val code: String,
    val state: String? = null,
)
