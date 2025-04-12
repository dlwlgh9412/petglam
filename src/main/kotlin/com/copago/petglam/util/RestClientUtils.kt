package com.copago.petglam.util

import com.copago.petglam.exception.ApiException
import com.copago.petglam.exception.ExternalApiException
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient
import java.util.function.Consumer

class RestClientUtils {
    companion object {
        /**
         * GET 요청 수행 및 응답 파싱
         */
        inline fun <reified T> get(
            restClient: RestClient,
            uri: String,
            providerName: String,
            headers: Consumer<HttpHeaders>? = null
        ): T {
            return try {
                val requestSpec = restClient.get()
                    .uri(uri)

                if (headers != null) {
                    requestSpec.headers(headers)
                }

                requestSpec.retrieve()
                    .onStatus({ statusCode -> statusCode.is4xxClientError || statusCode.is5xxServerError }) { _, response ->
                        val errorBody = response.body.toString()
                        val statusCode = response.statusCode.value()
                        throw ExternalApiException(
                            "$providerName API 요청 실패 ($statusCode): $errorBody",
                            statusCode,
                            providerName
                        )
                    }
                    .body(object : ParameterizedTypeReference<T>() {})
                    ?: throw ApiException("$providerName API에서 응답을 받을 수 없습니다.", 500)
            } catch (e: ExternalApiException) {
                throw e
            } catch (e: Exception) {
                throw ExternalApiException("$providerName API 요청 중 오류 발생: ${e.message}", 500, providerName)
            }
        }

        inline fun <reified T> post(
            restClient: RestClient,
            uri: String,

        )
    }
}