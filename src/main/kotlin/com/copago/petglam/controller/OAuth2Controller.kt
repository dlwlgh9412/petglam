package com.copago.petglam.controller

import com.copago.petglam.context.PetglamRequestContext
import com.copago.petglam.model.AuthCodeRequest
import com.copago.petglam.model.AuthResponse
import com.copago.petglam.service.OAuth2ClientService
import com.copago.petglam.service.UserService
import jakarta.validation.Valid
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
    @PostMapping("/callback/{provider}")
    fun handleOAuth2Callback(
        @PathVariable provider: String,
        @Valid @RequestBody request: AuthCodeRequest
    ): ResponseEntity<AuthResponse> {
        val requestId = PetglamRequestContext.getRequestId()
        log.info("Processing OAuth2 callback for provider: {} [requestId={}]", provider, requestId)

        try {
            // 소셜 로그인 처리 및 사용자 정보 획득
            val userProfile = oAuth2ClientService.processOAuth2Login(
                provider,
                request.code,
                request.state
            )

            // 사용자 처리 및 JWT 토큰 생성
            val authResponse = userService.processSocialLogin(userProfile)

            log.info("OAuth2 login successful for provider: {} [requestId={}]", provider, requestId)
            return ResponseEntity.ok(authResponse)
        } catch (e: Exception) {
            // 로깅 처리는 GlobalExceptionHandler에서 수행되므로 여기서는 예외를 그대로 던집니다.
            log.warn("OAuth2 callback processing failed for provider: {} [requestId={}]", provider, requestId)
            throw e
        }
    }
}