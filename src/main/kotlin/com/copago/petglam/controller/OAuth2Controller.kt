package com.copago.petglam.controller

import com.copago.petglam.context.PetglamRequestContext
import com.copago.petglam.dto.AuthResponse
import com.copago.petglam.service.OAuth2ClientService
import com.copago.petglam.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/oauth2")
class OAuth2Controller(
    private val oAuth2ClientService: OAuth2ClientService,
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(OAuth2Controller::class.java)

    /**
     * 소셜 로그인 URL 생성
     */
    @GetMapping("/url/{provider}")
    fun loginPageUrl(@PathVariable provider: String): ResponseEntity<Map<String, String>> {
        val requestId = PetglamRequestContext.getRequestId()
        log.info("Generating login URL for provider: {} [requestId={}]", provider, requestId)

        val url = oAuth2ClientService.generateLoginUrl(provider)
        return ResponseEntity.ok(mapOf("url" to url))
    }

    /**
     * 소셜 로그인 콜백 처리
     */
    @GetMapping("/callback/{provider}")
    fun handleOAuth2Callback(
        @PathVariable provider: String,
        @RequestParam code: String,
        @RequestParam state: String?
    ): ResponseEntity<AuthResponse> {
        val requestId = PetglamRequestContext.getRequestId()
        log.info("Processing OAuth2 callback for provider: {} [requestId={}]", provider, requestId)

        // 소셜 로그인 처리 및 사용자 정보 획득
        val userProfile = oAuth2ClientService.processOAuth2Login(provider, code, state)

        // 사용자 처리 및 JWT 토큰 생성
        val authResponse = userService.processSocialLogin(userProfile)

        log.info("OAuth2 login successful for provider: {} [requestId={}]", provider, requestId)
        return ResponseEntity.ok(authResponse)
    }
}