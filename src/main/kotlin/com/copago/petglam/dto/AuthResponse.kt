package com.copago.petglam.dto

import com.copago.petglam.model.UserProfile

data class AuthResponse(
    val token: String,
    val user: UserProfile
)
