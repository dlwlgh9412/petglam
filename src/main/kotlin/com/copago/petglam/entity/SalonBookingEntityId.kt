package com.copago.petglam.entity

import java.io.Serializable
import java.time.LocalDateTime

data class SalonBookingEntityId(
    val partitionKey: Int,
    val bookingDateTime: LocalDateTime,
    var userEntity: UserEntity? = null,
    var salonEntity: SalonEntity? = null,
    var staffEntity: SalonStaffEntity? = null,
) : Serializable
