package com.copago.petglam.service

import com.copago.petglam.config.OAuth2Config
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class OAuth2ClientService(
    private val oAuth2Config: OAuth2Config,
    private val restClient: RestClient
) {

}