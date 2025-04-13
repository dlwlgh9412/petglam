package com.copago.petglam.service

import com.copago.petglam.context.PetglamRequestContext
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.stereotype.Service
import java.util.*

@Service
class ErrorMessageService(
    private val messageSource: MessageSource
) {
    fun getMessage(code: String, args: Array<Any>? = null): String {
        val locale = getLocale()

        return try {
            messageSource.getMessage(code, args, locale)
        } catch (e: NoSuchMessageException) {
            code
        }
    }

    private fun getLocale(): Locale {
        val localeCode = PetglamRequestContext.getLocale()
        return when (localeCode) {
            else -> Locale.KOREAN
        }
    }
}