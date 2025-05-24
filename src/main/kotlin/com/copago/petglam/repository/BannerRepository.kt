package com.copago.petglam.repository

import com.copago.petglam.entity.BannerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BannerRepository : JpaRepository<BannerEntity, Long> {
    @Query(value = "select b " +
            "from BannerEntity b " +
            "where b.isActive = true " +
            "order by coalesce(b.displayOrder, b.id)")
    fun findAllActiveBanners(): List<BannerEntity>
}