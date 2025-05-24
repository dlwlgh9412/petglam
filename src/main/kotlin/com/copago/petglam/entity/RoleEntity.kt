package com.copago.petglam.entity

import com.copago.petglam.enums.RoleType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tb_roles")
class RoleEntity(
    @Id
    val id: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    val name: RoleType,
)