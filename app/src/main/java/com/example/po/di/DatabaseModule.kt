package com.example.po.di

import android.content.Context
import androidx.room.Room
import com.example.po.data.local.PODatabase
import com.example.po.data.local.keystore.DBKeyManager
import com.example.po.data.local.dao.ChatMessageDao
import com.example.po.data.local.dao.MoodEntryDao
import com.example.po.data.local.dao.ConfessionDao
import com.example.po.data.local.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDBKeyManager(@ApplicationContext context: Context): DBKeyManager {
        return DBKeyManager(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        keyManager: DBKeyManager
    ): PODatabase {
        return Room.databaseBuilder(
            context,
            PODatabase::class.java,
            "po_database"
        ).build()
    }

    @Provides
    fun provideChatMessageDao(db: PODatabase): ChatMessageDao = db.chatMessageDao

    @Provides
    fun provideMoodEntryDao(db: PODatabase): MoodEntryDao = db.moodEntryDao

    @Provides
    fun provideConfessionDao(db: PODatabase): ConfessionDao = db.confessionDao

    @Provides
    fun provideUserProfileDao(db: PODatabase): UserProfileDao = db.userProfileDao
}
