package com.copago.petglam.dto

import com.copago.petglam.entity.SalonBookingEntity
import com.copago.petglam.enums.BookingStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class SalonBookingRequest(
    val bookingDateTime: LocalDateTime,
    val services: List<Long>,
) {
    fun toEntity(): SalonBookingEntity {
        return SalonBookingEntity(
            partitionKey = bookingDateTime.year * bookingDateTime.monthValue,
            bookingDateTime = bookingDateTime,
            status = BookingStatus.REQUESTED,
            totalPrice = BigDecimal.valueOf(0)
        )
    }
}
