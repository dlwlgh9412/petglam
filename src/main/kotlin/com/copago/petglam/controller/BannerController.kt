package com.copago.petglam.controller

import com.copago.petglam.dto.BannerResponse
import com.copago.petglam.service.BannerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/banners")
class BannerController(
    private val bannerService: BannerService
) {
    @GetMapping
    fun getAllBanners(): ResponseEntity<BannerResponse> {
        return ResponseEntity.ok(bannerService.getBanners())
    }
}