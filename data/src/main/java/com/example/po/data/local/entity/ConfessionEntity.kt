package com.example.po.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.po.domain.model.Emotion

@Entity(tableName = "confession")
data class ConfessionEntity(
    @PrimaryKey val id: String,
    val text: String,
    val emotionTag: Emotion,
    val timestamp: Long
)
