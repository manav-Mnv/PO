package com.example.po.domain.model

data class Confession(
    val id: String,
    val text: String,
    val emotionTag: Emotion,
    val timestamp: Long
)
