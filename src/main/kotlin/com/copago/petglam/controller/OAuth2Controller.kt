package com.copago.petglam.controller

import com.copago.petglam.model.AuthCodeRequest
import com.copago.petglam.model.AuthResponse
import com.copago.petglam.service.OAuth2ClientService
import com.copago.petglam.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/oauth2")
class OAuth2Controller(
    private val oAuth2ClientService: OAuth2ClientService,
    private val userService: UserService
) {
    /**
     * 소셜 로그인 URL 생성
     */
    @GetMapping("/url/{provider}")
    fun loginPageUrl(@PathVariable provider: String): ResponseEntity<Map<String, String>> {
        val url = oAuth2ClientService.generateLoginUrl(provider)
        return ResponseEntity.ok(mapOf("url" to url))
    }

    /**
     * 소셜 로그인 콜백 처리
     */
    @PostMapping("/callback/{provider}")
    fun handleOAuth2Callback(
        @PathVariable provider: String,
        @RequestBody request: AuthCodeRequest
    ): ResponseEntity<AuthResponse> {
        // 소셜 로그인 처리 및 사용자 정보 획득
        val userProfile = oAuth2ClientService.processOAuth2Login(
            provider,
            request.code,
            request.state
        )
        
        // 사용자 처리 및 JWT 토큰 생성
        val authResponse = userService.processSocialLogin(userProfile)
        
        return ResponseEntity.ok(authResponse)
    }
}
