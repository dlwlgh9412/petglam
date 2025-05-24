package com.copago.petglam.repository

import com.copago.petglam.enums.OSType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RedisAppVersionRepository(
    private val redisTemplate: StringRedisTemplate
) : AppVersionRepository {
    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${app.version.redis-key-prefix}")
    private lateinit var keyPrefix: String

    private val fallbackCache = mutableMapOf<OSType, String?>()
    private var lastSuccessfulReadTime: Long = 0
    private val cacheTtlMillis = TimeUnit.MINUTES.toMillis(11)

    override fun findMinSupportedVersion(os: OSType): String? {
        val key = "$keyPrefix${os.name.lowercase()}"
        val version = redisTemplate.opsForValue().get(key)

        if (version != null) {
            updateFallbackCache(os, version)
        }

        return version
    }

    @Synchronized
    private fun updateFallbackCache(os: OSType, version: String) {
        fallbackCache[os] = version
        lastSuccessfulReadTime = System.currentTimeMillis()
    }

    @Synchronized
    private fun getFromFallbackCache(os: OSType): String? {
        if (System.currentTimeMillis() - lastSuccessfulReadTime > cacheTtlMillis) {
            log.warn("{} 캐시가 오래되었습니다. null 을 반환합니다.", os)
            return null
        }

        return fallbackCache[os]
    }
}