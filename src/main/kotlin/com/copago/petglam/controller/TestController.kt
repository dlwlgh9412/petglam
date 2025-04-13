package com.copago.petglam.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/test")
class TestController {
    
    /**
     * 인증이 필요한 테스트 엔드포인트
     */
    @GetMapping("/auth")
    fun authenticatedEndpoint(request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val userId = request.getAttribute("userId") as String
        val userRoles = request.getAttribute("userRoles") as List<String>
        
        return ResponseEntity.ok(
            mapOf(
                "message" to "인증된 요청입니다.",
                "userId" to userId,
                "roles" to userRoles
            )
        )
    }
}
