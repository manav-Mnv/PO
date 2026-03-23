package com.example.po.domain.usecase

import com.example.po.domain.model.ChatMessage
import com.example.po.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(content: String) {
        val userMessage = ChatMessage(content = content, isFromUser = true)
        repository.sendMessage(userMessage)
        
        // Simulating AI response for now
        val aiMessage = ChatMessage(content = "Echo: $content", isFromUser = false)
        repository.sendMessage(aiMessage)
    }
}
