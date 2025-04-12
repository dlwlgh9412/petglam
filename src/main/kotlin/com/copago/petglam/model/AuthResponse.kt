package com.copago.petglam.model

data class AuthResponse(
    val token: String,
    val user: UserProfile
)
