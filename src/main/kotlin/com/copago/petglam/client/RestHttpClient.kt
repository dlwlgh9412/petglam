package com.copago.petglam.client

import com.copago.petglam.context.PetglamRequestContext
import com.copago.petglam.exception.HttpClientException
import com.copago.petglam.exception.InfrastructureException
import com.copago.petglam.exception.enums.InfrastructureErrorCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class RestHttpClient(
    private val restClient: RestClient,
    private val retryTemplate: RetryTemplate
) : HttpClient {
    private val log: Logger = LoggerFactory.getLogger(RestHttpClient::class.java)

    /**
     * GET 요청 수행
     */
    override fun <T : Any> get(uri: String, responseType: Class<T>, headers: Map<String, String>?): T {
        val requestId = PetglamRequestContext.getRequestId()
        val targetUri = URI.create(uri)
        log.info("Starting GET request to {} [requestId={}]", uri, requestId)

        return try {
            retryTemplate.execute<T, RuntimeException> {
                val requestSpec = restClient.get().uri(uri)
                headers?.forEach { (key, value) -> requestSpec.header(key, value) }

                requestSpec.retrieve()
                    .onStatus({ statusCode -> statusCode.isError }) { request, response ->
                        handleErrorStatus(HttpMethod.GET, targetUri, response, requestId)
                    }
                    .body(responseType)
            }
        } catch (e: HttpClientException) {
            log.error(
                "HTTP 요청(GET) 중 오류가 발생하였습니다. {} [requestId={}]: Status={}, Message={}",
                targetUri,
                requestId,
                e.statusCode,
                e.message,
                e
            )
            throw e
        } catch (e: RestClientException) {
            throw HttpClientException(
                method = HttpMethod.GET,
                uri = targetUri,
                statusCode = null,
                message = "HTTP 요청(GET) 중 오류가 발생하였습니다: ${e.message}",
                requestId = requestId,
                cause = e
            )
        } catch (e: Exception) {
            log.error("Unexpected error during GET request to {} [requestId={}]", targetUri, requestId, e)
            throw InfrastructureException(
                InfrastructureErrorCode.EXTERNAL_SERVICE_ERROR,
                message = "HTTP 요청(GET) 중 오류가 발생하였습니다: ${e.message}",
                cause = e
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
        val requestId = PetglamRequestContext.getRequestId()
        val targetUri = URI.create(uri) // Use URI object
        log.info("Starting POST request to {} [requestId={}]", uri, requestId)

        return try {
            retryTemplate.execute<R, RuntimeException> {
                val requestSpec = restClient.post().uri(uri).contentType(contentType)
                headers?.forEach { (key, value) -> requestSpec.header(key, value) }

                if (body != null) {
                    requestSpec.body(body)
                }

                requestSpec.retrieve()
                    .onStatus({ statusCode -> statusCode.isError }) { request, response ->
                        handleErrorStatus(HttpMethod.POST, targetUri, response, requestId)
                    }
                    .body(responseType)
            }
        } catch (e: HttpClientException) {
            log.error(
                "HTTP 요청(POST) 중 오류가 발생하였습니다. {} [requestId={}]: Status={}, Message={}",
                targetUri,
                requestId,
                e.statusCode,
                e.message,
                e
            )
            throw e
        } catch (e: RestClientException) {
            throw HttpClientException(
                method = HttpMethod.POST,
                uri = targetUri,
                statusCode = null,
                message = "HTTP 요청(POST) 중 오류가 발생하였습니다: ${e.message}",
                requestId = requestId,
                cause = e
            )
        } catch (e: Exception) {
            log.error("Unexpected error during GET request to {} [requestId={}]", targetUri, requestId, e)
            throw InfrastructureException(
                InfrastructureErrorCode.EXTERNAL_SERVICE_ERROR,
                message = "HTTP 요청(POST) 중 오류가 발생하였습니다: ${e.message}",
                cause = e
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
        val requestId = PetglamRequestContext.getRequestId()
        val targetUri = URI.create(uri) // Use URI object

        log.info("Starting form POST request to {} [requestId={}]", uri, requestId)

        return try {
            retryTemplate.execute<R, RuntimeException> {
                val requestSpec = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                headers?.forEach { (key, value) -> requestSpec.header(key, value) }

                val formParams = formData.entries.joinToString("&") { (key, value) ->
                    "${URLEncoder.encode(key, StandardCharsets.UTF_8)}=${
                        URLEncoder.encode(
                            value,
                            StandardCharsets.UTF_8
                        )
                    }"
                }
                requestSpec.body(formParams)

                requestSpec.retrieve()
                    .onStatus({ statusCode -> statusCode.isError }) { request, response ->
                        handleErrorStatus(HttpMethod.POST, targetUri, response, requestId)
                    }
                    .body(responseType)
            }
        } catch (e: HttpClientException) {
            log.error(
                "HTTP 요청(POST) 중 오류가 발생하였습니다. {} [requestId={}]: Status={}, Message={}",
                targetUri,
                requestId,
                e.statusCode,
                e.message,
                e
            )
            throw e
        } catch (e: RestClientException) {
            throw HttpClientException(
                method = HttpMethod.POST,
                uri = targetUri,
                statusCode = null,
                message = "HTTP 요청(POST) 중 오류가 발생하였습니다: ${e.message}",
                requestId = requestId,
                cause = e
            )
        } catch (e: Exception) {
            log.error("Unexpected error during GET request to {} [requestId={}]", targetUri, requestId, e)
            throw InfrastructureException(
                InfrastructureErrorCode.EXTERNAL_SERVICE_ERROR,
                message = "HTTP 요청(POST) 중 오류가 발생하였습니다: ${e.message}",
                cause = e
            )
        }
    }

    /**
     * 오류 응답 처리
     */
    private fun handleErrorStatus(
        method: HttpMethod,
        uri: URI,
        response: ClientHttpResponse,
        requestId: String
    ) {
        val statusCode = response.statusCode.value()
        val rawBody = try {
            // response.body 는 한 번만 읽을 수 있으므로 주의
            response.body.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        } catch (e: IOException) {
            log.warn(
                "Failed to read error response body from {} [requestId={}, statusCode={}]",
                uri,
                requestId,
                statusCode,
                e
            )
            "[Error Body Not Readable: ${e.message}]"
        }

        log.warn(
            "HTTP request failed: Method={}, URI={}, Status={}, Body={} [requestId={}]",
            method, uri, statusCode, rawBody, requestId // 로그에는 원본 본문 포함
        )

        // HttpClientException 발생
        throw HttpClientException(
            method = method,
            uri = uri,
            statusCode = statusCode,
            rawBody = rawBody, // 예외 객체에 원본 본문 포함
            message = "HTTP request failed with status code $statusCode", // 기본 메시지
            requestId = requestId
            // cause는 여기서는 null, RestClientException 등에서 설정됨
        )
    }
}
