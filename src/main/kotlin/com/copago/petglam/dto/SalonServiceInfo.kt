package com.copago.petglam.dto

import com.copago.petglam.entity.SalonServiceEntity
import java.math.BigDecimal

data class SalonServiceInfo(
    val serviceName: String?,
    val serviceDescription: String?,
    val servicePrice: BigDecimal,
) {
    companion object {
        fun fromEntity(entity: SalonServiceEntity): SalonServiceInfo {
            return SalonServiceInfo(
                serviceName = entity.serviceName,
                serviceDescription = entity.serviceDescription,
                servicePrice = entity.servicePrice
            )
        }
    }
}
