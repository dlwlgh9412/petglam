package com.copago.petglam.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/oauth2")
class OAuth2Controller {

    @GetMapping("/url/{provider}")
    fun loginPageUrl(@PathVariable provider: String): ResponseEntity<String> {

    }
}