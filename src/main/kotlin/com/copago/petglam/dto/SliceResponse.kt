package com.copago.petglam.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "슬라이스 결과")
data class SliceResponse<T>(
    @Schema(description = "데이터")
    val data: List<T>,

    @Schema(description = "현재 슬라이스 번호", example = "0")
    val pageNumber: Int,

    @Schema(description = "슬라이스 당 항목 수", example = "10")
    val pageSize: Int,

    @Schema(description = "다음 슬라이스 존재 여부", example = "true")
    val hasNext: Boolean,

    @Schema(description = "첫 슬라이스 여부", example = "true")
    val isFirst: Boolean,
)
