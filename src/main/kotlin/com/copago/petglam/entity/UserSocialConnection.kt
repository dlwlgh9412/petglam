package com.copago.petglam.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_social_connections",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_provider_provider_id",
            columnNames = ["provider", "providerId"]
        ),
        UniqueConstraint(
            name = "uk_user_provider",
            columnNames = ["user_id", "provider"]
        )
    ]
)
class UserSocialConnection(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val provider: String,

    @Column(nullable = false)
    val providerId: String,

    @Column(nullable = true)
    var accessToken: String? = null,

    @Column(nullable = true)
    var refreshToken: String? = null,

    @Column(nullable = true)
    var tokenExpiry: LocalDateTime? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun updateTokens(accessToken: String?, refreshToken: String?, expiresIn: Int?) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.tokenExpiry = expiresIn?.let { LocalDateTime.now().plusSeconds(it.toLong()) }
        this.updatedAt = LocalDateTime.now()
    }
}
