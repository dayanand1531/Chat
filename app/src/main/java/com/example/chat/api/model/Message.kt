package com.example.chat.api.model

import com.example.chat.util.MessageType

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val messageType: MessageType,
    val content: String,
    val timestamp: Long
)
