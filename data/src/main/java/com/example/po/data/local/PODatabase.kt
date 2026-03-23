package com.example.po.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.po.data.local.dao.ChatMessageDao
import com.example.po.data.local.dao.ConfessionDao
import com.example.po.data.local.dao.MoodEntryDao
import com.example.po.data.local.dao.UserProfileDao
import com.example.po.data.local.entity.ChatMessageEntity
import com.example.po.data.local.entity.ConfessionEntity
import com.example.po.data.local.entity.MoodEntryEntity
import com.example.po.data.local.entity.UserProfileEntity

@Database(
    entities = [
        ChatMessageEntity::class,
        MoodEntryEntity::class,
        ConfessionEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PODatabase : RoomDatabase() {
    abstract val chatMessageDao: ChatMessageDao
    abstract val moodEntryDao: MoodEntryDao
    abstract val confessionDao: ConfessionDao
    abstract val userProfileDao: UserProfileDao
}
