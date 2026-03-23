package com.example.po.data.repository

import com.example.po.data.local.dao.ChatDao
import com.example.po.data.mapper.toDomain
import com.example.po.data.mapper.toEntity
import com.example.po.domain.model.ChatMessage
import com.example.po.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val dao: ChatDao
) : ChatRepository {

    override fun getMessages(): Flow<List<ChatMessage>> {
        return dao.getMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun sendMessage(message: ChatMessage) {
        // In a real app, this would also call an API
    }

    // Wait, the interface had suspend for sendMessage
    override suspend fun sendMessage(message: ChatMessage) {
        dao.insertMessage(message.toEntity())
    }

    override suspend fun clearChat() {
        dao.clearChat()
    }
}
