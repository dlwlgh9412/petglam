package com.copago.petglam.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "tb_pets")
class PetEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val userEntity: UserEntity,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "breed", nullable = false)
    var breed: String,

    @Column(name = "birth_date")
    var birthDate: LocalDate? = null,

    @Column(name = "weight")
    var weight: BigDecimal? = null,

    @Column(name = "gender")
    var gender: String? = null,

    @Column(name = "neutered_status", nullable = false)
    var neuteredStatus: Boolean = false,

    @Column(name = "special_notes", columnDefinition = "TEXT")
    var specialNotes: String? = null,

    @Column(name = "profile_image_url", nullable = false)
    var imageUrl: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)