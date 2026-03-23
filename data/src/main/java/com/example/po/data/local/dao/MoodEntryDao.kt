package com.example.po.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.po.data.local.entity.MoodEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodEntryEntity)

    @Query("SELECT * FROM mood_entry ORDER BY timestamp DESC")
    fun getAllMoods(): Flow<List<MoodEntryEntity>>

    @Delete
    suspend fun deleteMood(mood: MoodEntryEntity)
}
