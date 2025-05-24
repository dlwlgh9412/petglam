package com.copago.petglam.repository

import com.copago.petglam.entity.SalonEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SalonRepository : JpaRepository<SalonEntity, Long>, JpaSpecificationExecutor<SalonEntity> {
    @Query(
        """
        select salon
        from SalonEntity salon
        left join fetch salon.services service
        left join fetch salon.images image
        left join fetch salon.staffs staff
        where salon.id = :id
    """
    )
    fun findSalonDetailsById(@Param("id") id: Long): SalonEntity?
}