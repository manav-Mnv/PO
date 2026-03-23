package com.example.po.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val content: String,
    val timestamp: Long,
    val isFromUser: Boolean
)
