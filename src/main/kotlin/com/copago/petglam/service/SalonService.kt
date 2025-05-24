package com.copago.petglam.service

import com.copago.petglam.dto.SalonDetailResponse
import com.copago.petglam.dto.SalonServiceInfo
import com.copago.petglam.dto.SalonStaffInfo
import com.copago.petglam.dto.SalonSummary
import com.copago.petglam.dto.SliceResponse
import com.copago.petglam.entity.SalonEntity
import com.copago.petglam.exception.ResourceNotFoundException
import com.copago.petglam.repository.SalonRepository
import com.copago.petglam.util.SalonSpecifications
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SalonService(
    private val repository: SalonRepository
) {
    @Transactional(readOnly = true)
    fun findSalons(
        page: Int,
        size: Int,
        sort: List<String>?,
        latitude: Double?,
        longitude: Double?,
        radiusKm: Double?,
        city: String?,
        district: String?,
        searchQuery: String?
    ): SliceResponse<SalonSummary> {
        val sortObj = parseSort(sort)
        val pageable: Pageable = PageRequest.of(page, size, sortObj)

        var spec: Specification<SalonEntity> = Specification.where(null)
        spec = spec.and(SalonSpecifications.withDynamicQuery(searchQuery, city, district))
        spec = spec.and(SalonSpecifications.withLocation(latitude, longitude, radiusKm))

        val salonEntitySlice: Slice<SalonEntity> = repository.findAll(spec, pageable)

        val data = salonEntitySlice.content.mapNotNull { salon -> SalonSummary.convert(salon) }

        return SliceResponse(
            data = data,
            pageNumber = salonEntitySlice.number,
            pageSize = salonEntitySlice.size,
            hasNext = salonEntitySlice.hasNext(),
            isFirst = salonEntitySlice.isFirst,
        )
    }

    @Transactional(readOnly = true)
    fun findSalonById(id: Long): SalonDetailResponse {
        val salon = repository.findSalonDetailsById(id)

        salon?.let {
            return SalonDetailResponse(
                id = salon.id!!,
                name = salon.name,
                contact = salon.contact,
                description = salon.description,
                streetAddress = salon.streetAddress,
                services = salon.services.filter { it.isOffered }.map { SalonServiceInfo.fromEntity(it) },
                staffs = salon.staffs.map { SalonStaffInfo.fromEntity(it) }
            )
        }

        throw ResourceNotFoundException("요청하진 샵 정보를 찾을 수 없습니다. (id: $id)")
    }

    private fun parseSort(sortParams: List<String>?): Sort {
        if (sortParams.isNullOrEmpty()) {
            return Sort.by(Sort.Direction.DESC, "id") // 기본 정렬 최신 순
        }

        val orders = sortParams.mapNotNull { sortParam ->
            val parts = sortParam.split(",")
            when (parts.size) {
                2 -> {
                    val property = parts[0]
                    val direction = Sort.Direction.fromString(parts[1])
                    Sort.Order(direction, property)
                }

                1 -> {
                    Sort.Order(Sort.Direction.ASC, parts[0])
                }

                else -> {
                    null
                }
            }
        }

        return if (orders.isNotEmpty()) Sort.by(orders) else Sort.by(Sort.Direction.DESC, "id")
    }
}