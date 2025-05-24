package com.copago.petglam.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "tb_salon_services")
class SalonServiceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false, updatable = false)
    var salonEntity: SalonEntity? = null,

    @Column(name = "service_name", nullable = false)
    var serviceName: String? = null,

    @Column(name = "service_description", columnDefinition = "TEXT")
    var serviceDescription: String? = null,

    @Column(name = "service_price", nullable = false)
    var servicePrice: BigDecimal,

    @Column(name = "is_offered", nullable = false)
    var isOffered: Boolean = false
)