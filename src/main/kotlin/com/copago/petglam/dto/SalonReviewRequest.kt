package com.copago.petglam.dto

enum class SalonReviewRequest(
    val comment: String?,
    val staffId: Long? = null,
    val imageUrls: List<String>? = null,
)