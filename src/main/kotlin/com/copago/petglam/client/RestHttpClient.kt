package com.copago.petglam.client

import com.copago.petglam.context.RequestContextHolder
import com.copago.petglam.exception.ErrorCode
import com.copago.petglam.exception.ExternalApiException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class RestHttpClient(
    private val restClient: RestClient,
    private val objectMapper: ObjectMapper,
    private val retryTemplate: RetryTemplate
) : HttpClient {
    private val log: Logger = LoggerFactory.getLogger(RestHttpClient::class.java)

    /**
     * GET 요청 수행
     */
    override fun <T : Any> get(uri: String, responseType: Class<T>, headers: Map<String, String>?): T {
        val requestId = RequestContextHolder.getRequestId()
        log.info("Starting GET request to {} [requestId={}]", uri, requestId)

        return try {
            retryTemplate.execute<T, ExternalApiException> {
                val requestSpec = restClient.get().uri(uri)

                if (headers != null) {
                    requestSpec.headers { headerValues ->
                        headers.forEach { (key, value) ->
                            headerValues.add(key, value)
                        }

                        headerValues.add("X-Request-ID", requestId)
                    }
                }

                requestSpec.retrieve()
                    .onStatus({ response -> response.is4xxClientError || response.is5xxServerError }) { request, response ->
                        handleErrorResponse(uri, response, requestId)
                    }
                    .body(responseType) ?: throw ExternalApiException(
                    uri,
                    "API 응답을 받을 수 없습니다.",
                    emptyMap(),
                    ErrorCode.API_COMMUNICATION_ERROR,
                    requestId
                )
            }
        } catch (e: ExternalApiException) {
            log.error("GET request failed to {} [requestId={}]: {}", uri, requestId, e.message)
            throw e
        } catch (e: Exception) {
            log.error("Unexpected error in GET request to {} [requestId={}]", uri, requestId, e)
            throw ExternalApiException(
                uri,
                "API 요청 중 오류 발생: ${e.message}",
                emptyMap(),
                ErrorCode.API_CLIENT_ERROR,
                requestId,
                e
            )
        }
    }

    /**
     * POST 요청 수행
     */
    override fun <T : Any, R : Any> post(
        uri: String,
        body: T?,
        responseType: Class<R>,
        contentType: MediaType,
        headers: Map<String, String>?
    ): R {
        val requestId = RequestContextHolder.getRequestId()
        log.info("Starting POST request to {} [requestId={}]", uri, requestId)

        return try {
            retryTemplate.execute<R, ExternalApiException> {
                val requestSpec = restClient.post().uri(uri).contentType(contentType)

                if (headers != null) {
                    requestSpec.headers { headerValues ->
                        headers.forEach { (key, value) ->
                            headerValues.add(key, value)
                        }
                        // 요청 추적을 위한 헤더 추가
                        headerValues.add("X-Request-ID", requestId)
                    }
                }

                if (body != null) {
                    requestSpec.body(body)
                }

                requestSpec.retrieve()
                    .onStatus({ statusCode -> statusCode.is4xxClientError || statusCode.is5xxServerError }) { request, response ->
                        handleErrorResponse(uri, response, requestId)
                    }.body(responseType) ?: throw ExternalApiException(
                    uri,
                    "API 응답을 받을 수 없습니다.",
                    emptyMap(),
                    ErrorCode.API_COMMUNICATION_ERROR,
                    requestId
                )
            }
        } catch (e: ExternalApiException) {
            log.error("POST request failed to {} [requestId={}]: {}", uri, requestId, e.message)
            throw e
        } catch (e: Exception) {
            log.error("Unexpected error in POST request to {} [requestId={}]", uri, requestId, e)
            throw ExternalApiException(
                uri,
                "API 요청 중 오류 발생: ${e.message}",
                emptyMap(),
                ErrorCode.API_COMMUNICATION_ERROR,
                requestId,
                e
            )
        }
    }

    /**
     * Form URL Encoded POST 요청 수행
     */
    override fun <R : Any> postFormUrlEncoded(
        uri: String,
        formData: Map<String, String>,
        responseType: Class<R>,
        headers: Map<String, String>?
    ): R {
        val requestId = RequestContextHolder.getRequestId()
        log.info("Starting form POST request to {} [requestId={}]", uri, requestId)

        return try {
            retryTemplate.execute<R, ExternalApiException> {
                val requestSpec = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)

                if (headers != null) {
                    requestSpec.headers { headerValues ->
                        headers.forEach { (key, value) ->
                            headerValues.add(key, value)
                        }
                        // 요청 추적을 위한 헤더 추가
                        headerValues.add("X-Request-ID", requestId)
                    }
                }

                val formParams = formData.entries.joinToString("&") { (key, value) ->
                    "${URLEncoder.encode(key, StandardCharsets.UTF_8)}=${URLEncoder.encode(value, StandardCharsets.UTF_8)}"
                }
                requestSpec.body(formParams)

                requestSpec.retrieve()
                    .onStatus({ statusCode -> statusCode.is4xxClientError || statusCode.is5xxServerError }) { request, response ->
                        handleErrorResponse(uri, response, requestId)
                    }
                    .body(responseType)
                    ?: throw ExternalApiException(
                        uri,
                        "API 응답을 받을 수 없습니다.",
                        emptyMap(),
                        ErrorCode.API_COMMUNICATION_ERROR,
                        requestId
                    )
            }
        } catch (e: ExternalApiException) {
            log.error("Form POST request failed to {} [requestId={}]: {}", uri, requestId, e.message)
            throw e
        } catch (e: Exception) {
            log.error("Unexpected error in form POST request to {} [requestId={}]", uri, requestId, e)
            throw ExternalApiException(
                uri,
                "API 요청 중 오류 발생: ${e.message}",
                emptyMap(),
                ErrorCode.API_COMMUNICATION_ERROR,
                requestId,
                e
            )
        }
    }

    /**
     * 오류 응답 처리
     */
    private fun handleErrorResponse(uri: String, response: ClientHttpResponse, requestId: String) {
        val statusCode = response.statusCode.value()

        val errorBody = try {
            val buffer = ByteArray(response.body.available())
            response.body.read(buffer)
            String(buffer, Charsets.UTF_8)
        } catch (e: Exception) {
            "응답 본문을 읽을 수 없습니다."
        }

        // 응답 로깅
        log.warn("API error response from {}: status={}, body={} [requestId={}]",
            uri, statusCode, errorBody, requestId)

        val errorDetails = try {
            val mappedBody = objectMapper.readValue(errorBody, Map::class.java) as Map<String, Any>
            mappedBody + mapOf("statusCode" to statusCode)
        } catch (e: Exception) {
            mapOf(
                "rawErrorResponse" to errorBody,
                "statusCode" to statusCode
            )
        }

        // 상태 코드에 따른 적절한 오류 코드 결정
        val errorCode = when (statusCode) {
            in 400..499 -> ErrorCode.API_CLIENT_ERROR
            in 500..599 -> ErrorCode.API_SERVER_ERROR
            else -> ErrorCode.API_COMMUNICATION_ERROR
        }

        throw ExternalApiException(
            uri,
            "API 요청 실패 ($statusCode)",
            errorDetails,
            errorCode,
            requestId
        )
    }
}
