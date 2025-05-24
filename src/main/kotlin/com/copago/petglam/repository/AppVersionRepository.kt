package com.copago.petglam.repository

import com.copago.petglam.enums.OSType

interface AppVersionRepository {
    fun findMinSupportedVersion(os: OSType): String?
}