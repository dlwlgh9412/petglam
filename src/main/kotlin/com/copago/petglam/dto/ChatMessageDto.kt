package com.copago.petglam.dto

import java.time.LocalDateTime

data class ChatMessageDto(
    val messageId: String? = null,
    val roomId: String,
    val senderId: Long,
    val senderName: String? = null,
    val senderProfileImageUrl: String? = null,
    val messageType: String = "TEXT",
    val content: String,
    val sentAt: LocalDateTime = LocalDateTime.now()
)
