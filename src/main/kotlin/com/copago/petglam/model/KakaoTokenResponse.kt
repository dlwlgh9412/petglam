package com.copago.petglam.model

data class KakaoTokenResponse (
    val accessToken: String,
    val tokenType: String,
    val refreshToken: String,
    val expiresIn: Int,
    val scope: String? = null,
)