package com.copago.petglam.chat.controller

import com.copago.petglam.chat.service.ChatMessagePersistenceService
import com.copago.petglam.chat.service.RedisChatMessagePublisher
import com.copago.petglam.dto.ChatMessageDto
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import java.time.LocalDateTime
import java.util.UUID

@Controller
class ChatMessageController(
    private val redisPublisher: RedisChatMessagePublisher,
    private val persistenceService: ChatMessagePersistenceService
) {
    @MessageMapping("/chat/send/{roomId}")
    fun sendMessage(@DestinationVariable roomId: String,
                    @Payload content: Any) {
        val userId = 1L // 임시
        if (userId == null) {
            return
        }

        val senderName = "User $userId"
        val sendProfileImageUrl = "https://example.com/profile.jpg"

        val chatMessage = ChatMessageDto(
            messageId = UUID.randomUUID().toString(),
            roomId = roomId,
            senderId = userId,
            senderName = senderName,
            senderProfileImageUrl = sendProfileImageUrl,
            content = content.toString(),
            sentAt = LocalDateTime.now()
        )

        redisPublisher.publish(chatMessage)
        persistenceService.saveMessageAsync(chatMessage)
    }
}