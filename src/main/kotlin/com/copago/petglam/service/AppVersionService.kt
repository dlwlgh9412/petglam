package com.copago.petglam.service

import com.copago.petglam.enums.OSType
import com.copago.petglam.exception.BaseException
import com.copago.petglam.repository.AppVersionRepository
import com.copago.petglam.util.VersionComparator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AppVersionService(
    private val appVersionRepository: AppVersionRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${app.version.fail-safe-min-version}")
    private lateinit var failSafeMinVersion: String

    @Value("\${app.version.user-agent-ios-keyword:iphone|ipad|ipod}")
    private lateinit var iosKeywords: String
    private val iosPattern by lazy { Regex(iosKeywords, RegexOption.IGNORE_CASE) }

    @Value("\${app.version.user-agent-android-keyword:android}")
    private lateinit var androidKeywords: String
    private val androidPattern by lazy { Regex(androidKeywords, RegexOption.IGNORE_CASE) }

    fun checkSupport(clientVersion: String?, userAgent: String?, osTypeHeader: String?) {
        if (clientVersion.isNullOrBlank()) {
            return
        }

        val osType = determineOsType(userAgent, osTypeHeader)
        if (osType == OSType.UNKNOWN) {
            throw IllegalArgumentException("요청 헤더로부터 OS 타입을 추출할 수 없습니다.")
        }

        // 1. Redis 에서 해당 OS 의 최소 지원 버전 조회
        var minSupportedVersion = appVersionRepository.findMinSupportedVersion(osType)

        // 2. Redis 조회 실패 및 Fallback 캐시도 실패/만료 시 Fail-Safe 값 사용
        if (minSupportedVersion == null) {
            minSupportedVersion = failSafeMinVersion.ifBlank { null }
        }

        if (minSupportedVersion != null) {
            if (VersionComparator.compare(clientVersion, minSupportedVersion) < 0) {
                throw BaseException.upgradeRequired(clientVersion, minSupportedVersion)
            }
        } else {

        }
    }

    // User-Agent 또는 전용 헤더를 기반으로 OS 타입 식별
    private fun determineOsType(userAgent: String?, osTypeHeader: String?): OSType {
        if (!osTypeHeader.isNullOrBlank()) {
            return when (osTypeHeader.lowercase()) {
                "ios" -> OSType.IOS
                "android" -> OSType.ANDROID
                else -> OSType.UNKNOWN
            }
        }

        if (!userAgent.isNullOrBlank()) {
            return when {
                iosPattern.containsMatchIn(userAgent) -> OSType.IOS
                androidPattern.containsMatchIn(userAgent) -> OSType.ANDROID
                else -> OSType.UNKNOWN
            }
        }

        return OSType.UNKNOWN
    }
}