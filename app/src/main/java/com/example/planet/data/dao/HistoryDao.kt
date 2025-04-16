package com.example.planet.data.dao

import androidx.room.*
import com.example.planet.data.entity.History

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Query("SELECT * FROM history WHERE userId = :userId")
    suspend fun getHistoryByUser(userId: Int): List<History>
}
