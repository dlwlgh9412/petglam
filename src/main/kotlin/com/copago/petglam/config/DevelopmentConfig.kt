package com.copago.petglam.config

import com.copago.petglam.entity.SalonEntity
import com.copago.petglam.entity.UserEntity
import com.copago.petglam.entity.UserSocialAccountEntity
import com.copago.petglam.repository.BannerRepository
import com.copago.petglam.repository.SalonRepository
import com.copago.petglam.repository.UserRepository
import com.copago.petglam.repository.UserSocialAccountRepository
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Configuration
class DevelopmentConfig {
    @Bean
    fun geometryFactory(): GeometryFactory {
        return GeometryFactory(PrecisionModel(), 4326)
    }
}

//@Component
//@Profile("dev", "local")
class DevProfileConfig(
    private val salonRepository: SalonRepository,
    private val userRepository: UserRepository,
    private val userSocialAccountRepository: UserSocialAccountRepository,
    private val bannerRepository: BannerRepository,
    private val geometryFactory: GeometryFactory
) : CommandLineRunner {
    @Transactional
    override fun run(vararg args: String?) {
        val user1 = userRepository.save(
            UserEntity(
                email = "testuser1@example.com",
                name = "김테스트",
                profileImageUrl = "https://example.com/profile1.jpg",
                isEmailVerified = true,
                lastLoginAt = LocalDateTime.now()
            )
        )

        userSocialAccountRepository.save(
            UserSocialAccountEntity(
                userEntity = user1,
                provider = "KAKAO",
                providerId = "kakao_123456789",
                accessToken = "kakao_access_token_example",
                refreshToken = "kakao_refresh_token_example",
                tokenExpiry = LocalDateTime.now().plusHours(1)
            )
        )

        val user2 = userRepository.save(
            UserEntity(
                email = "testuser2@example.com",
                name = "이테스트",
                profileImageUrl = "https://example.com/profile2.jpg",
                isEmailVerified = true,
                lastLoginAt = LocalDateTime.now()
            )
        )

        userSocialAccountRepository.save(
            UserSocialAccountEntity(
                userEntity = user2,
                provider = "KAKAO",
                providerId = "kakao_1234567890",
                accessToken = "kakao_access_token_example",
                refreshToken = "kakao_refresh_token_example",
                tokenExpiry = LocalDateTime.now().plusHours(1)
            )
        )

        val salonEntity1 = salonRepository.save(
            SalonEntity(
                name = "도그넷 애견미용실",
                userEntity = user2,
                contact = "02-111-1111",
                description = "애견미용 15년 경력으로 이쁘고 안전하게 미용해 드린답니다.. ***\n고양이미용 경력15년 대형견미용 경력15년",
                streetAddress = "서울 관악구 장군봉1길 36",
                city = "서울",
                district = "관악구",
                postalCode = "08784",
                location = geometryFactory.createPoint(Coordinate(126.94203598573063,37.48161988327875)),
                imageUrl = "https://search.pstatic.net/common/?src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20200315_196%2F1584231641109iLSo7_PNG%2F_JiwFcHDV7iiPUGYASAgPdd7.png"
            )
        )

//        salonEntity1.addImage(SalonImageEntity(imageUrl = "https://example.com/images/salon1_img1.jpg"))
//        salonEntity1.addImage(SalonImageEntity(imageUrl = "https://example.com/images/salon1_img2.jpg"))
//        salonRepository.save(salonEntity1)

        val salonEntity2 = salonRepository.save(
            SalonEntity(
                name = "유니독 애견미용실",
                userEntity = user1,
                contact = "02-222-2222",
                description = "오픈형 반려견 미용실\n" +
                        "가게 앞 주차. cctv완비\n" +
                        "예약제로 운영됩니다",
                streetAddress = "서울 영등포구 신길로 235 1층",
                city = "서울",
                district = "영등포구",
                postalCode = "07307",
                location = geometryFactory.createPoint(Coordinate(126.910401190398, 37.5131100288859)), // 홍대입구역 부근
                imageUrl = "https://example.com/images/salon2_main.jpg"
            )
        )
//
//        salonEntity2.addImage(SalonImageEntity(imageUrl = "https://example.com/images/salon2_img1.jpg"))
//        salonRepository.save(salonEntity2)

        val salonEntity3 = salonRepository.save(
            SalonEntity(
                name = "이로애견미용실",
                userEntity = user1,
                contact = "02-222-2222",
                description = "반려견미용 .미용용품 .\n" +
                        "탄산입욕제 . 탄산스파 .",
                streetAddress = "서울 관악구 남부순환로 1678 2층",
                city = "서울",
                district = "관악구",
                postalCode = "08780",
                location = geometryFactory.createPoint(Coordinate(126.936831700613, 37.484226175994)), // 홍대입구역 부근
                imageUrl = "https://example.com/images/salon3_main.jpg"
            )
        )
    }
}