package com.copago.petglam.service

import com.copago.petglam.dto.BannerResponse
import com.copago.petglam.repository.BannerRepository
import org.springframework.stereotype.Service

@Service
class BannerService(
    private val repository: BannerRepository
) {
    fun getBanners(): BannerResponse {
        val banners = repository.findAllActiveBanners()

        val data = emptyArray<BannerResponse.Data>()
        banners.forEach { it ->
            {
                data.plus(
                    BannerResponse.Data(
                        imageUrl = it.imageUrl,
                        targetUrl = it.targetUrl,
                        title = it.title,
                        type = it.bannerType,
                        description = it.description,
                        displayOrder = it.displayOrder
                    )
                )
            }
        }

        return BannerResponse(data = data.toList())
    }
}