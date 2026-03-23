package com.example.po.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.po.domain.model.Sender

@Entity(tableName = "chat_message")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val text: String,
    val sender: Sender,
    val timestamp: Long
)
