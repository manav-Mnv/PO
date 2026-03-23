package com.example.po.data.di

import android.content.Context
import androidx.room.Room
import com.example.po.data.local.ChatDatabase
import com.example.po.data.local.dao.ChatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            "chat_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: ChatDatabase): ChatDao {
        return database.chatDao()
    }
}
