package com.copago.petglam.dto

data class SalonDetailResponse (
    val id: Long,
    val name: String,
    val contact: String,
    var description: String? = null,
    val streetAddress: String,
    val services: List<SalonServiceInfo> = listOf(),
    val staffs: List<SalonStaffInfo> = listOf(),
)