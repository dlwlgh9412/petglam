package com.copago.petglam.repository

import com.copago.petglam.entity.UserEntity
import com.copago.petglam.entity.UserSocialAccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserSocialAccountRepository : JpaRepository<UserSocialAccountEntity, Long> {
    fun findByProviderAndProviderId(provider: String, providerId: String): UserSocialAccountEntity?
    fun findByUserEntityAndProvider(userEntity: UserEntity, provider: String): UserSocialAccountEntity?
}
