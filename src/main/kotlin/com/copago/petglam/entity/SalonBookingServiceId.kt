package com.copago.petglam.entity

import java.io.Serializable
import java.time.LocalDateTime

data class SalonBookingServiceId(
    val partitionKey: Int,
    val bookingDateTime: LocalDateTime,
    val bookingEntity: SalonBookingEntity,
    val userEntity: UserEntity,
    val salonEntity: SalonEntity,
    val staffEntity: SalonStaffEntity,
    val serviceEntity: SalonServiceEntity,
) : Serializable
