package com.copago.petglam.service

import com.copago.petglam.entity.UserEntity
import com.copago.petglam.entity.UserSocialAccountEntity
import com.copago.petglam.exception.UserException
import com.copago.petglam.dto.AuthResponse
import com.copago.petglam.model.UserProfile
import com.copago.petglam.repository.UserRepository
import com.copago.petglam.repository.UserSocialAccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userSocialAccountRepository: UserSocialAccountRepository,
    private val jwtService: JwtService
) {
    /**
     * 소셜 로그인으로 사용자 처리 및 JWT 발급
     */
    @Transactional
    fun processSocialLogin(userProfile: UserProfile): AuthResponse {
        val user = findOrCreateSocialUser(userProfile)
        val accessToken = jwtService.generateAccessToken(
            user.id.toString(),
            user.email,
            listOf("USER")
        )
        val refreshToken = jwtService.generateRefreshToken(user.id.toString())

        user.updateRefreshToken(refreshToken)
        user.updateLastLoginAt()
        userRepository.save(user)

        return AuthResponse(
            token = accessToken,
            user = UserProfile(
                id = user.id.toString(),
                name = user.name,
                email = user.email,
                picture = user.profileImageUrl,
                provider = userProfile.provider
            )
        )
    }

    /**
     * 소셜 로그인 사용자 조회 또는 생성
     */
    @Transactional
    fun findOrCreateSocialUser(profile: UserProfile): UserEntity {
        val providerId = profile.id ?: throw IllegalArgumentException("소셜 ID가 없습니다.")
        val provider = profile.provider ?: throw IllegalArgumentException("소셜 제공자 정보가 없습니다.")

        // 기존 소셜 연결 확인
        val existingSocialConnection = userSocialAccountRepository.findByProviderAndProviderId(provider, providerId)

        // 기존 소셜 연결이 있으면 해당 사용자 반환
        existingSocialConnection?.let {
            val user = it.userEntity
            profile.name?.let { name ->
                profile.picture?.let { picture ->
                    user.updateProfile(name, picture)
                }
            }

            return userRepository.save(user)
        }

        // 이메일로 기존 사용자 확인
        val email = profile.email
        if (!email.isNullOrBlank()) {
            val existingUser = userRepository.findByEmail(email)

            existingUser?.let { user ->
                // 소셜 연결 추가
                val socialAccount = UserSocialAccountEntity(
                    userEntity = user,
                    provider = provider,
                    providerId = providerId
                )

                user.socialAccounts.add(socialAccount)

                profile.name?.let { name ->
                    profile.picture?.let { picture ->
                        user.updateProfile(name, picture)
                    }
                }
                return userRepository.save(user)
            }
        }

        // 새 사용자 생성
        val newUserEntity = UserEntity(
            email = email ?: "$providerId@$provider.account",
            name = profile.name ?: "사용자",
            profileImageUrl = profile.picture,
            isEmailVerified = email != null
        )

        val savedUser = userRepository.save(newUserEntity)

        // 소셜 연결 추가
        val socialConnection = UserSocialAccountEntity(
            userEntity = savedUser,
            provider = provider,
            providerId = providerId
        )
        savedUser.socialAccounts.add(socialConnection)

        return userRepository.save(savedUser)
    }

    /**
     * ID로 사용자 조회
     */
    fun getUserById(id: Long): UserEntity {
        return userRepository.findById(id).orElseThrow { UserException.notFound() }
    }

    /**
     * 이메일로 사용자 조회
     */
    fun getUserByEmail(email: String): UserEntity {
        return userRepository.findByEmail(email) ?: throw UserException.notFound()
    }
}
