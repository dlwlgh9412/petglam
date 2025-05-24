package com.copago.petglam.entity

import com.copago.petglam.dto.BannerUpdate
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_banners")
class BannerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "image_url", nullable = false)
    var imageUrl: String,

    @Column(name = "target_url", nullable = false)
    var targetUrl: String,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description", nullable = false)
    var description: String,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = false,

    @Column(name = "banner_type", nullable = false)
    val bannerType: String,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun activate() {
        this.isActive = true
        this.updatedAt = LocalDateTime.now()
    }

    fun deactivate() {
        this.isActive = false
        this.updatedAt = LocalDateTime.now()
    }

    fun update(bannerUpdate: BannerUpdate) {
        this.imageUrl = bannerUpdate.imageUrl ?: this.imageUrl
        this.targetUrl = bannerUpdate.targetUrl ?: this.targetUrl
        this.title = bannerUpdate.title ?: this.title
        this.description = bannerUpdate.description ?: this.description
        this.updatedAt = LocalDateTime.now()
    }
}