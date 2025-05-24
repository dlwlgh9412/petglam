package com.copago.petglam.chat.service

import com.copago.petglam.dto.ChatMessageDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class RedisChatMessageSubscriber(
    private val messageTemplate: SimpMessagingTemplate,
    private val objectMapper: ObjectMapper
) : MessageListener {
    private val log = LoggerFactory.getLogger(RedisChatMessageSubscriber::class.java)

    override fun onMessage(message: Message, pattern: ByteArray?) {
        try {
            val publishedMessageJson = message.toString()
            log.info("Message received: {}", publishedMessageJson)

            val chatMessageDto = objectMapper.readValue(publishedMessageJson, ChatMessageDto::class.java)
            val destination = "/topic/chat/room/${chatMessageDto.roomId}"
            messageTemplate.convertAndSend(destination, chatMessageDto)
        } catch (e: Exception) {
            log.error("Error while processing message: {}", e.message)
        }
    }
}