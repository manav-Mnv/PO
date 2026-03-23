package com.example.po.domain.model

data class MoodEntry(
    val id: String,
    val emotion: Emotion,
    val timestamp: Long
)
