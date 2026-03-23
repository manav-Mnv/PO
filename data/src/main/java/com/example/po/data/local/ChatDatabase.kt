package com.example.po.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.po.data.local.dao.ChatDao
import com.example.po.data.local.entity.MessageEntity

@Database(entities = [MessageEntity::class], version = 1)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}
