package com.example.po.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.po.data.local.entity.MoodEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoods(): Flow<List<MoodEntryEntity>>

    @Insert
    suspend fun insertMood(mood: MoodEntryEntity)
}
