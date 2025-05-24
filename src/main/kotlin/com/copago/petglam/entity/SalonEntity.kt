package com.copago.petglam.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

@Entity
@Table(name = "tb_salons")
class SalonEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var userEntity: UserEntity,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "contact", nullable = false)
    val contact: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    // 도로명 주소
    @Column(name = "street_address", nullable = false)
    val streetAddress: String,

    // 시/도
    @Column(name = "city", nullable = false)
    val city: String,

    // 시/군/구
    @Column(name = "district", nullable = false)
    val district: String,

    // 우편번호
    @Column(name = "postal_code", nullable = false)
    val postalCode: String,

    @Column(name = "location", nullable = false, columnDefinition = "POINT SRID 4326")
    val location: Point,

    // 대표이미지
    @Column(name = "image_url", nullable = false)
    val imageUrl: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "salonEntity", cascade = [CascadeType.ALL])
    val services: MutableSet<SalonServiceEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "salonEntity", cascade = [CascadeType.ALL])
    val staffs: MutableSet<SalonStaffEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "salonEntity", cascade = [CascadeType.ALL])
    val images: MutableSet<SalonImageEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "salonEntity")
    val bookings: MutableSet<SalonBookingEntity> = mutableSetOf()
) {
    fun addService(service: SalonServiceEntity) {
        this.services.add(service)
        service.salonEntity = this
    }

    fun addStaff(staff: SalonStaffEntity) {
        this.staffs.add(staff)
        staff.salonEntity = this
    }

    fun addImage(image: SalonImageEntity) {
        this.images.add(image)
        image.salonEntity = this
    }

    fun addBooking(booking: SalonBookingEntity) {
        this.bookings.add(booking)
        booking.salonEntity = this
    }
}