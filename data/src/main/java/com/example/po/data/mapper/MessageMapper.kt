package com.example.po.data.mapper

import com.example.po.data.local.entity.MessageEntity
import com.example.po.domain.model.ChatMessage

fun MessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        content = content,
        timestamp = timestamp,
        isFromUser = isFromUser
    )
}

fun ChatMessage.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        content = content,
        timestamp = timestamp,
        isFromUser = isFromUser
    )
}
