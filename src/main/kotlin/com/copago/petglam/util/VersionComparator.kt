package com.copago.petglam.util

import org.apache.maven.artifact.versioning.ComparableVersion
import org.slf4j.LoggerFactory

object VersionComparator {
    private val log = LoggerFactory.getLogger(javaClass)

    fun compare(v1: String?, v2: String?): Int {
        if (v1.isNullOrBlank() || v2.isNullOrBlank()) {
            log.warn("비교가 불가능한 버전입니다. v1={}, v2{}", v1, v2)
            throw IllegalArgumentException("비교를 위한 버전 문자열은 null 또는 빈 값이 될 수 없습니다.");
        }

        val comparableV1 = ComparableVersion(v1)
        val comparableV2 = ComparableVersion(v2)
        return comparableV1.compareTo(comparableV2)
    }
}