package com.example.po.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.po.data.local.entity.ConfessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfession(confession: ConfessionEntity)

    @Query("SELECT * FROM confession ORDER BY timestamp DESC")
    fun getAllConfessions(): Flow<List<ConfessionEntity>>

    @Delete
    suspend fun deleteConfession(confession: ConfessionEntity)
}
