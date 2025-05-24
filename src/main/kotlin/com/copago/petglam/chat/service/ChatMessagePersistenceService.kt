package com.copago.petglam.chat.service

import com.copago.petglam.dto.ChatMessageDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ChatMessagePersistenceService {
    private val log = LoggerFactory.getLogger(ChatMessagePersistenceService::class.java)

    fun saveMessageAsync(message: ChatMessageDto) {
        log.info("Saving message: {}", message)
    }
}