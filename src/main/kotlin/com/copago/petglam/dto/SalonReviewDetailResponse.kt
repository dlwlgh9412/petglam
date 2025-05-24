package com.copago.petglam.dto

import com.copago.petglam.entity.SalonReviewEntity
import java.time.LocalDateTime

data class SalonReviewDetailResponse(
    val reviewId: Long,
    val salonId: Long,
    val userId: Long,
    val userName: String,
    val userProfileImageUrl: String?,
    val comment: String?,
    val staffId: Long?,
    val staffName: String?,
    val staffPosition: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val images: List<String>
) {
    companion object {
        fun fromEntity(entity: SalonReviewEntity): SalonReviewDetailResponse {
            return SalonReviewDetailResponse(
                reviewId = entity.id!!,
                salonId = entity.salonEntity?.id!!,
                userId = entity.userEntity?.id!!,
                userName = entity.userEntity?.name ?: "알 수 없음",
                userProfileImageUrl = entity.userEntity?.profileImageUrl,
                comment = entity.comment,
                staffId = entity.staff?.id,
                staffName = entity.staff?.name,
                staffPosition = entity.staff?.position,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                images = entity.images.map { it.imageUrl }
            )
        }
    }
}
