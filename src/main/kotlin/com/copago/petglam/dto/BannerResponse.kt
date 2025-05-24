package com.copago.petglam.dto

data class BannerResponse(
    val data: List<Data>
) {
    data class Data(
        val imageUrl: String,
        val targetUrl: String,
        val title: String,
        val type: String,
        val description: String,
        val displayOrder: Int? = null,
    )
}
