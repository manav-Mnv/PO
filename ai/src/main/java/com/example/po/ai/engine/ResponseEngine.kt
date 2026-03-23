package com.example.po.ai.engine

import kotlinx.coroutines.flow.Flow

interface ResponseEngine {
    fun generateResponse(prompt: String): Flow<String>
}
