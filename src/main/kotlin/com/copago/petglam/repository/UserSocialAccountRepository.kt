package com.copago.petglam.repository

import com.copago.petglam.entity.User
import com.copago.petglam.entity.UserSocialAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserSocialAccountRepository : JpaRepository<UserSocialAccount, Long> {
    fun findByProviderAndProviderId(provider: String, providerId: String): UserSocialAccount?
    fun findByUserAndProvider(user: User, provider: String): UserSocialAccount?
}
