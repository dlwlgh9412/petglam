package com.copago.petglam.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_salon_staff")
class SalonStaffEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "salon_id")
    var salonEntity: SalonEntity? = null,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "position")
    var position: String? = null,

    @Column(name = "profile_image_url", nullable = false)
    val profileImageUrl: String,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "staffEntity")
    val bookings: MutableSet<SalonBookingEntity> = mutableSetOf()
) {
    fun addBooking(booking: SalonBookingEntity) {
        this.bookings.add(booking)
        booking.staffEntity = this
    }
}