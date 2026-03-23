package com.example.po.data.local

import androidx.room.TypeConverter
import com.example.po.domain.model.Emotion
import com.example.po.domain.model.Sender

class Converters {
    @TypeConverter
    fun fromEmotion(emotion: Emotion): String = emotion.name
    @TypeConverter
    fun toEmotion(value: String): Emotion = Emotion.valueOf(value)
    @TypeConverter
    fun fromSender(sender: Sender): String = sender.name
    @TypeConverter
    fun toSender(value: String): Sender = Sender.valueOf(value)
}
