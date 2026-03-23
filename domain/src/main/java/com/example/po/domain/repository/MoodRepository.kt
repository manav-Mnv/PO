package com.example.po.domain.repository

import com.example.po.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface MoodRepository {
    fun getMoodHistory(): Flow<List<String>>
    suspend fun recordMood(emotion: String)
}
