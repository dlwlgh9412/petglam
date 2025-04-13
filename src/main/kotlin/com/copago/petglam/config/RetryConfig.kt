package com.copago.petglam.config

import com.copago.petglam.exception.ExternalApiException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryPolicy
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

@Configuration
class RetryConfig {
    @Bean
    fun retryTemplate(): RetryTemplate {
        val retryTemplate = RetryTemplate()

        retryTemplate.setRetryPolicy(retryPolicy())

        val backOffPolicy = ExponentialBackOffPolicy()
        backOffPolicy.initialInterval = 1000L
        backOffPolicy.multiplier = 2.0
        backOffPolicy.maxInterval = 10000L
        retryTemplate.setBackOffPolicy(backOffPolicy)

        return retryTemplate
    }

    /**
     * 재시도 정책 정의
     */
    private fun retryPolicy(): RetryPolicy {
        val exceptionMap = HashMap<Class<out Throwable>, Boolean>()

        // 재시도 가능한 예외 유형 지정
        exceptionMap[SocketTimeoutException::class.java] = true
        exceptionMap[TimeoutException::class.java] = true
        exceptionMap[ExternalApiException::class.java] = true

        return object : SimpleRetryPolicy(3, exceptionMap, true) {
            override fun canRetry(context: RetryContext): Boolean {
                val throwable = context.lastThrowable

                if (throwable is ExternalApiException) {
                    return throwable.isRetryable() && super.canRetry(context)
                }

                return super.canRetry(context)
            }
        }
    }
}