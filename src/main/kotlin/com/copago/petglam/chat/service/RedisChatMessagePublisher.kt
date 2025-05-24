package com.copago.petglam.chat.service

import com.copago.petglam.dto.ChatMessageDto
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service

@Service
class RedisChatMessagePublisher(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val chatMessageTopic: ChannelTopic
) {
    private val log = LoggerFactory.getLogger(RedisChatMessagePublisher::class.java)

    fun publish(chatMessage: ChatMessageDto) {
        log.info("Publishing message: {}", chatMessage)
        redisTemplate.convertAndSend(chatMessageTopic.topic, chatMessage)
    }
}