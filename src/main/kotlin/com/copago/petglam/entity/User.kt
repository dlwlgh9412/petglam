package com.copago.petglam.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_user_email", columnList = "email", unique = true)
    ]
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

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

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val socialConnections: MutableSet<UserSocialConnection> = mutableSetOf()
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
}
