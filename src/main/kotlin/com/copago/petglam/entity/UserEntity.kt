package com.copago.petglam.entity

import jakarta.persistence.*
import org.hibernate.annotations.Formula
import java.time.LocalDateTime

@Entity
@Table(name = "tb_users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = true)
    var password: String? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var profileImageUrl: String? = null,

    @Column(nullable = false)
    var isEmailVerified: Boolean = false,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    var lastLoginAt: LocalDateTime? = null,

    @Column(nullable = true)
    var refreshToken: String? = null,

    @OneToMany(mappedBy = "userEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    val socialAccounts: MutableSet<UserSocialAccountEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "userEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    val roles: MutableSet<UserRoleEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "userEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    val bookings: MutableSet<SalonBookingEntity> = mutableSetOf(),
) {
    fun updateProfile(name: String?, profileImageUrl: String?) {
        name?.let { this.name = it }
        profileImageUrl?.let { this.profileImageUrl = it }
        this.updatedAt = LocalDateTime.now()
    }

    fun updateRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken
        this.updatedAt = LocalDateTime.now()
    }

    fun updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now()
        this.updatedAt = LocalDateTime.now()
    }

    fun addRole(role: RoleEntity) {
        val userRole = UserRoleEntity(userEntity = this, roleEntity = role)
        this.roles.add(userRole)
    }

    fun addSalonBooking(booking: SalonBookingEntity) {
        this.bookings.add(booking)
        booking.userEntity = this
    }
}
