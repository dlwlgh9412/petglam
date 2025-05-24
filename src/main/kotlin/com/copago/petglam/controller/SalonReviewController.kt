package com.copago.petglam.controller

import com.copago.petglam.service.SalonReviewService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/salons")
class SalonReviewController(
    private val salonReviewService: SalonReviewService,
) {
    @GetMapping("/{id}/reviews")
    fun getSalonReviews(@PathVariable id: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(salonReviewService.findAllReviews(id))
    }
}