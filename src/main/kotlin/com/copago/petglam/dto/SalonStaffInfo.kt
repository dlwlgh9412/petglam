package com.copago.petglam.dto

import com.copago.petglam.entity.SalonStaffEntity

data class SalonStaffInfo(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val position: String,
) {
    companion object {
        fun fromEntity(entity: SalonStaffEntity): SalonStaffInfo {
            return SalonStaffInfo(
                id = entity.id!!,
                name = entity.name,
                imageUrl = entity.profileImageUrl,
                position = entity.position ?: ""
            )
        }
    }
}
