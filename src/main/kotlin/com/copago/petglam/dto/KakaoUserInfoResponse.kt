package com.copago.petglam.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoUserInfoResponse(
    val id: Long,

    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount
) {
    data class KakaoAccount(
        val profile: KakaoProfile? = null,
        val email: String? = null,

        @JsonProperty("is_email_verified")
        val isEmailVerified: Boolean? = null
    ) {
        data class KakaoProfile(
            val nickname: String? = null,

            @JsonProperty("profile_image_url")
            val profileImageUrl: String? = null,

            @JsonProperty("thumbnail_image_url")
            val thumbnailImageUrl: String? = null
        )
    }
}

