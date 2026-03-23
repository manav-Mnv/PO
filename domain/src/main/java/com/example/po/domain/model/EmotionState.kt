package com.example.po.domain.model

data class EmotionState(
    val primaryEmotion: String,
    val confidence: Float,
    val intensity: Float
)
