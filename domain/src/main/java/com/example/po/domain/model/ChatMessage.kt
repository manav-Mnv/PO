package com.example.po.domain.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromUser: Boolean
)
