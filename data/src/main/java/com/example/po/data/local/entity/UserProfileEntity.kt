package com.example.po.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.po.domain.model.Emotion

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "default_user",
    val preferredStyle: String,
    val dominantEmotion: Emotion,
    val sessionCount: Int
)
