package com.example.po.domain.repository

import com.example.po.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(): Flow<List<ChatMessage>>
    suspend fun sendMessage(message: ChatMessage)
    suspend fun clearChat()
}
