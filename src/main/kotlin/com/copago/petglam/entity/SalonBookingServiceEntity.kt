package com.copago.petglam.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinColumns
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "tb_salon_booking_services")
@IdClass(SalonBookingServiceId::class)
class SalonBookingServiceEntity(
    @Id
    @Column(name = "partition_key", nullable = false)
    val partitionKey: Int,

    @Id
    @Column(name = "booking_date_time", nullable = false)
    val bookingDateTime: LocalDateTime,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        value = [JoinColumn(name = "partition_key", nullable = false, updatable = false),
            JoinColumn(name = "booking_date_time", nullable = false, updatable = false),
            JoinColumn(name = "user_id", nullable = false, updatable = false),
            JoinColumn(name = "salon_id", nullable = false, updatable = false),
            JoinColumn(name = "staff_id", nullable = false, updatable = false)]
    )
    var bookingEntity: SalonBookingEntity? = null,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    var userEntity: UserEntity? = null,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false, updatable = false)
    var salonEntity: SalonEntity? = null,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false, updatable = false)
    var staffEntity: SalonStaffEntity? = null,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false, updatable = false)
    var serviceEntity: SalonServiceEntity? = null,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "price_at_booking", nullable = false)
    val priceAtBooking: BigDecimal
)

