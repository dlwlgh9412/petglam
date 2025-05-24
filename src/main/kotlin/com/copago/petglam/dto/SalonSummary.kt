package com.copago.petglam.dto

import com.copago.petglam.entity.SalonEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "샵 목록 요약 정보")
data class SalonSummary(
    @Schema(description = "샵 ID", example = "1")
    val id: Long,

    @Schema(description = "샵 이름", example = "새끼 강아지 미용실")
    val name: String,

    @Schema(description = "대표 이미지 URL", example = "https://example.com/image.jpg")
    val imageUrl: String,

    @Schema(description = "도로명 주소", example = "서울특별시 관악구 ...")
    val streetAddress: String,
) {
    companion object {
        fun convert(salonEntity: SalonEntity): SalonSummary? {
            return salonEntity.id?.let {
                SalonSummary(
                    id = it,
                    name = salonEntity.name,
                    imageUrl = salonEntity.imageUrl,
                    streetAddress = salonEntity.streetAddress
                )
            }
        }
    }
}
