package com.copago.petglam.dto

import java.time.LocalDateTime

data class ChatRoomInfo(
    val roomId: String,
    val roomName: String,
    val lastMessage: String? = null,
    val lastMessageAt: LocalDateTime? = null,
    val unreadCount: Int,
    val opponentProfileImageUrl: String?
)
