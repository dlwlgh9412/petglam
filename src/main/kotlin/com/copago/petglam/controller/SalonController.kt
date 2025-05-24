package com.copago.petglam.controller

import com.copago.petglam.dto.SalonDetailResponse
import com.copago.petglam.dto.SalonSummary
import com.copago.petglam.dto.SliceResponse
import com.copago.petglam.service.SalonService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/salons")
@Tag(name = "Salons", description = "미용실 정보 API")
@Validated
class SalonController(
    private val salonService: SalonService,
) {
    @Operation(
        summary = "미용실 목록 조회 (커서 기반)",
        description = "커서 기반 미용실 목록 조회",
        responses = [
            ApiResponse(
                responseCode = "200", description = "성공", content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = SliceResponse::class))
                ]
            ),
        ]
    )
    @Parameters(
        Parameter(name = "size", description = "페이지 당 항목 수"),
        Parameter(name = "sort", description = "정렬 기준(property, direction)"),
        Parameter(name = "latitude", description = "위도"),
        Parameter(name = "longitude", description = "경도"),
        Parameter(name = "radiusKm", description = "검색 범위(km 단위)"),
        Parameter(name = "city", description = "시/도"),
        Parameter(name = "district", description = "시/군/구"),
        Parameter(name = "searchQuery", description = "필터 조건")

    )
    @GetMapping
    fun getSalonList(
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) size: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) latitude: Double?,
        @RequestParam(required = false) longitude: Double?,
        @RequestParam(required = false, defaultValue = "5.0") @Min(0) radiusKm: Double?,
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) district: String?,
        @RequestParam(required = false) searchQuery: String?
    ): ResponseEntity<SliceResponse<SalonSummary>> {
        val result = salonService.findSalons(page, size, sort, latitude, longitude, radiusKm, city, district, searchQuery)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{id}")
    fun getSalonDetail(@PathVariable id: Long): ResponseEntity<SalonDetailResponse> {
        return ResponseEntity.ok(salonService.findSalonById(id))
    }
}