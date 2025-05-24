package com.copago.petglam.repository

import com.copago.petglam.entity.SalonReviewEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SalonReviewRepository : JpaRepository<SalonReviewEntity, Long> {
    @Query("""
        select review,
        (select count(*) from SalonReviewEntity sr where sr.userEntity.id = review.userEntity.id)
        from SalonReviewEntity review
        join fetch review.userEntity user
        where review.salonEntity.id = :salonId
        order by review.id desc
    """)
    fun findAllReviews(salonId: Long): List<SalonReviewEntity>
}