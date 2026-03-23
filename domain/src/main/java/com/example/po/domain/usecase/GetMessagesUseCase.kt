package com.example.po.domain.usecase

import com.example.po.domain.model.ChatMessage
import com.example.po.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<List<ChatMessage>> = repository.getMessages()
}
