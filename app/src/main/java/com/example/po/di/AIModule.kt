package com.example.po.di

import com.example.po.response.DefaultResponseEngineFactory
import com.example.po.response.ResponseEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIModule {
    @Provides
    @Singleton
    fun provideResponseEngine(): ResponseEngine {
        return DefaultResponseEngineFactory.create()
    }

    @Provides
    @Singleton
    fun provideEmotionDetector(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
    ): com.example.po.ai.engine.EmotionDetector = com.example.po.ai.engine.ResilientEmotionDetector(context)
}
