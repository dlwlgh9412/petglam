package com.copago.petglam.client

import com.copago.petglam.exception.ExternalApiException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class RestHttpClient(
    private val restClient: RestClient,
    private val objectMapper: ObjectMapper
) : HttpClient {
    override fun <T : Any> get(uri: String, responseType: Class<T>, headers: Map<String, String>?): T {
        try {
            val requestSpec = restClient.get().uri(uri)

            if (headers != null) {
                requestSpec.headers { headerValues ->
                    {
                        headers.forEach { (key, value) ->
                            headerValues.add(key, value)
                        }
                    }
                }
            }

            return requestSpec.retrieve()
                .onStatus({ response -> response.is4xxClientError || response.is5xxServerError }) { request, response ->
                    handleErrorResponse(
                        uri,
                        response
                    )
                }
                .body(responseType) ?: throw ExternalApiException(
                uri,
                "API 응답을 받을 수 없습니다.",
                emptyMap(),
                500
            )
        } catch (e: ExternalApiException) {
            throw e
        } catch (e: Exception) {
            throw ExternalApiException(
                uri,
                "API 요청 중 오류 발생: ${e.message}",
                emptyMap(),
                500,
                e
            )
        }
    }

    override fun <T : Any, R : Any> post(
        uri: String,
        body: T?,
        responseType: Class<R>,
        contentType: MediaType,
        headers: Map<String, String>?
    ): R {
        try {
            val requestSpec = restClient.post().uri(uri).contentType(contentType)

            if (headers != null) {
                requestSpec.headers { headerValues ->
                    headers.forEach { (key, value) ->
                        headerValues.add(key, value)
                    }
                }
            }

            if (body != null) {
                requestSpec.body(body)
            }

            return requestSpec.retrieve()
                .onStatus({ statusCode -> statusCode.is4xxClientError || statusCode.is5xxServerError }) { request, response ->
                    handleErrorResponse(uri, response)
                }.body(responseType) ?: throw ExternalApiException(
                uri,
                "API 응답을 받을 수 없습니다.",
                emptyMap(),
                500
            )
        } catch (e: ExternalApiException) {
            throw e
        } catch (e: Exception) {
            throw ExternalApiException(
                uri,
                "API 요청 중 오류 발생: ${e.message}",
                emptyMap(),
                500,
                e
            )
        }
    }

    override fun <R : Any> postFormUrlEncoded(
        uri: String,
        formData: Map<String, String>,
        responseType: Class<R>,
        headers: Map<String, String>?
    ): R {
        try {
            val requestSpec = restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)

            if (headers != null) {
                requestSpec.headers { headerValues ->
                    headers.forEach { (key, value) ->
                        headerValues.add(key, value)
                    }
                }
            }

            val formParams = formData.entries.joinToString("&") { (key, value) ->
                "${URLEncoder.encode(key, StandardCharsets.UTF_8)}=${URLEncoder.encode(value, StandardCharsets.UTF_8)}"
            }
            requestSpec.body(formParams)

            return requestSpec.retrieve()
                .onStatus({ statusCode -> statusCode.is4xxClientError || statusCode.is5xxServerError }) { request, response ->
                    handleErrorResponse(uri, response)
                }
                .body(responseType)
                ?: throw ExternalApiException(
                    uri,
                    "API 응답을 받을 수 없습니다.",
                    emptyMap(),
                    500
                )
        } catch (e: ExternalApiException) {
            throw e
        } catch (e: Exception) {
            throw ExternalApiException(
                uri,
                "API 요청 중 오류 발생: ${e.message}",
                emptyMap(),
                500,
                e
            )
        }
    }

    private fun handleErrorResponse(uri: String, response: ClientHttpResponse) {
        val statusCode = response.statusCode.value()
        val errorBody = try {
            val buffer = ByteArray(response.body.available())
            response.body.read(buffer)
            String(buffer, Charsets.UTF_8)
        } catch (e: Exception) {
            "응답 본문을 읽을 수 없습니다."
        }

        val errorDetails = try {
            objectMapper.readValue(errorBody, Map::class.java) as Map<String, Any>
        } catch (e: Exception) {
            mapOf("rawErrorResponse" to errorBody)
        }

        throw ExternalApiException(
            uri,
            "API 요청 실패 ($statusCode)",
            errorDetails,
            statusCode
        )
    }
}