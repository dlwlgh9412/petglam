package com.copago.petglam.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "커서 페이지 결과")
data class CursorResponse<T>(
    @Schema(description = "현재 페이지 목록")
    val data: List<T>,

    @Schema(description = "다음 페이지 조회를 위한 정보")
    val nextCursor: Map<String, Any>?,

    @Schema(description = "현재 페이지 번호", example = "0")
    val pageNumber: Int,

    @Schema(description = "조회된 항목 수", example = "10")
    val size: Int,

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    val hasNext: Boolean
)
