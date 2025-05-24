package com.copago.petglam.service

import com.copago.petglam.dto.SalonReviewDetailResponse
import com.copago.petglam.dto.SalonReviewRequest
import com.copago.petglam.dto.SalonReviewResponse
import com.copago.petglam.entity.SalonReviewEntity
import com.copago.petglam.entity.SalonReviewImageEntity
import com.copago.petglam.exception.AuthorizationException
import com.copago.petglam.exception.ResourceNotFoundException
import com.copago.petglam.repository.SalonRepository
import com.copago.petglam.repository.SalonReviewRepository
import com.copago.petglam.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SalonReviewService(
    private val repository: SalonReviewRepository,
    private val salonRepository: SalonRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun findAllReviews(salonId: Long): List<SalonReviewResponse> {
        if (!salonRepository.existsById(salonId)) {
            throw ResourceNotFoundException("미용실 정보를 찾을 수 없습니다. (id: $salonId)")
        }
        val reviews = repository.findAllReviews(salonId) //
        return reviews.map { SalonReviewResponse.fromEntity(it) }
    }

    @Transactional
    fun saveReview(salonId: Long, userId: Long, request: SalonReviewRequest): SalonReviewDetailResponse {
        val salon = salonRepository.findById(salonId)
            .orElseThrow { ResourceNotFoundException("미용실 정보를 찾을 수 없습니다. (id: $salonId)") }

        val user = userRepository.getReferenceById(userId)

        val staffEntity = request.staffId?.let { staffId ->
            salon.staffs.find { it.id == staffId }
                ?: throw ResourceNotFoundException("해당 미용실의 스태프 정보를 찾을 수 없습니다. (salonId: $salonId, staffId: $staffId)")
        }

        val reviewEntity = SalonReviewEntity(
            salonEntity = salon,
            userEntity = user,
            staff = staffEntity,
            comment = request.comment
        )

        request.imageUrls?.forEach { imageUrl ->
            reviewEntity.images.add(SalonReviewImageEntity(imageUrl = imageUrl, salonReviewEntity = reviewEntity))
        }

        val savedReview = repository.save(reviewEntity)
        salonRepository.save(salon)

        return SalonReviewDetailResponse.fromEntity(savedReview)
    }

    @Transactional(readOnly = true)
    fun findReviewDetail(salonId: Long, reviewId: Long): SalonReviewDetailResponse {
        val review = repository.findById(reviewId)
            .orElseThrow { ResourceNotFoundException("리뷰를 찾을 수 없습니다. (reviewId: $reviewId)") }

        if (review.salonEntity?.id != salonId) {
            throw ResourceNotFoundException("해당 미용실의 리뷰가 아닙니다. (salonId: $salonId, reviewId: $reviewId)")
        }

        return SalonReviewDetailResponse.fromEntity(review)
    }

    @Transactional
    fun updateReview(
        salonId: Long,
        reviewId: Long,
        userId: Long,
        request: SalonReviewRequest
    ): SalonReviewDetailResponse {
        val review = repository.findById(reviewId)
            .orElseThrow { ResourceNotFoundException("리뷰를 찾을 수 없습니다. (reviewId: $reviewId)") }

        if (review.salonEntity?.id != salonId) {
            throw ResourceNotFoundException("해당 미용실의 리뷰가 아닙니다. (salonId: $salonId, reviewId: $reviewId)")
        }

        if (review.userEntity?.id != userId) {
            throw AuthorizationException("리뷰를 수정할 권한이 없습니다.")
        }

        val salon = review.salonEntity!!

        val staffEntity = request.staffId?.let { staffId ->
            salon.staffs.find { it.id == staffId }
                ?: throw ResourceNotFoundException("해당 미용실의 스태프 정보를 찾을 수 없습니다. (salonId: $salonId, staffId: $staffId)")
        }

        review.comment = request.comment
        review.staff = staffEntity
        review.updatedAt = LocalDateTime.now()

        review.images.clear()


        request.imageUrls?.forEach { imageUrl ->
            review.images.add(SalonReviewImageEntity(imageUrl = imageUrl, salonReviewEntity = review))
        }

        val updatedReview = repository.save(review)
        return SalonReviewDetailResponse.fromEntity(updatedReview)
    }

    @Transactional
    fun deleteReview(salonId: Long, reviewId: Long, userId: Long) {
        val review = repository.findById(reviewId)
            .orElseThrow { ResourceNotFoundException("리뷰를 찾을 수 없습니다. (reviewId: $reviewId)") }

        if (review.salonEntity?.id != salonId) {
            throw ResourceNotFoundException("해당 미용실의 리뷰가 아닙니다. (salonId: $salonId, reviewId: $reviewId)")
        }

        if (review.userEntity?.id != userId) {
            throw AuthorizationException("리뷰를 삭제할 권한이 없습니다.")
        }

        repository.delete(review)
    }
}