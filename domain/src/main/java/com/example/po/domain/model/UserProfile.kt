package com.example.po.domain.model

data class UserProfile(
    val preferredStyle: String,
    val dominantEmotion: Emotion,
    val sessionCount: Int
)
