package com.copago.petglam.model

data class OAuthTokenInfo(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val tokenType: String,
    val scope: String? = null
)