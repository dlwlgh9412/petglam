package com.copago.petglam.client

import org.springframework.http.MediaType

interface HttpClient {
    fun <T : Any> get(
        uri: String,
        responseType: Class<T>,
        headers: Map<String, String>? = null,
    ): T

    fun <T : Any, R : Any> post(
        uri: String,
        body: T?,
        responseType: Class<R>,
        contentType: MediaType = MediaType.APPLICATION_JSON,
        headers: Map<String, String>? = null,
    ): R

    fun <R : Any> postFormUrlEncoded(
        uri: String,
        formData: Map<String, String>,
        responseType: Class<R>,
        headers: Map<String, String>? = null
    ): R
}