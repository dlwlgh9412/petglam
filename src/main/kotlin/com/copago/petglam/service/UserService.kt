package com.copago.petglam.service

import com.copago.petglam.entity.User
import com.copago.petglam.entity.UserSocialConnection
import com.copago.petglam.exception.ResourceNotFoundException
import com.copago.petglam.model.AuthResponse
import com.copago.petglam.model.UserProfile
import com.copago.petglam.repository.UserRepository
import com.copago.petglam.repository.UserSocialConnectionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userSocialConnectionRepository: UserSocialConnectionRepository,
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
    fun findOrCreateSocialUser(profile: UserProfile): User {
        val providerId = profile.id ?: throw IllegalArgumentException("소셜 ID가 없습니다.")
        val provider = profile.provider ?: throw IllegalArgumentException("소셜 제공자 정보가 없습니다.")

        // 기존 소셜 연결 확인
        val existingSocialConnection = userSocialConnectionRepository
            .findByProviderAndProviderId(provider, providerId)

        // 기존 소셜 연결이 있으면 해당 사용자 반환
        if (existingSocialConnection.isPresent) {
            val user = existingSocialConnection.get().user
            
            // 프로필 정보 업데이트
            profile.name?.let { name ->
                profile.picture?.let { picture ->
                    user.updateProfile(name, picture)
                }
            }
            
            return userRepository.save(user)
        }

        // 이메일로 기존 사용자 확인
        val email = profile.email
        if (email != null && email.isNotBlank()) {
            val existingUser = userRepository.findByEmail(email)
            
            if (existingUser.isPresent) {
                val user = existingUser.get()
                
                // 소셜 연결 추가
                val socialConnection = UserSocialConnection(
                    user = user,
                    provider = provider,
                    providerId = providerId
                )
                user.socialConnections.add(socialConnection)
                
                // 프로필 정보 업데이트
                profile.name?.let { name ->
                    profile.picture?.let { picture ->
                        user.updateProfile(name, picture)
                    }
                }
                
                return userRepository.save(user)
            }
        }

        // 새 사용자 생성
        val newUser = User(
            email = email ?: "$providerId@$provider.account",
            name = profile.name ?: "사용자",
            profileImageUrl = profile.picture,
            isEmailVerified = email != null
        )
        
        val savedUser = userRepository.save(newUser)
        
        // 소셜 연결 추가
        val socialConnection = UserSocialConnection(
            user = savedUser,
            provider = provider,
            providerId = providerId
        )
        savedUser.socialConnections.add(socialConnection)
        
        return userRepository.save(savedUser)
    }

    /**
     * ID로 사용자 조회
     */
    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: $id") }
    }

    /**
     * 이메일로 사용자 조회
     */
    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow { ResourceNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다: $email") }
    }
}
