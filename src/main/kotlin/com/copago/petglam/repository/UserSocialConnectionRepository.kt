package com.copago.petglam.repository

import com.copago.petglam.entity.User
import com.copago.petglam.entity.UserSocialConnection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserSocialConnectionRepository : JpaRepository<UserSocialConnection, Long> {
    fun findByProviderAndProviderId(provider: String, providerId: String): Optional<UserSocialConnection>
    fun findByUserAndProvider(user: User, provider: String): Optional<UserSocialConnection>
}
