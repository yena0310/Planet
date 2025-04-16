package com.example.planet.data.dao

import androidx.room.*
import com.example.planet.data.entity.Quiz

@Dao
interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quiz: Quiz)

    @Query("SELECT * FROM quiz WHERE level = :level")
    suspend fun getQuizzesByLevel(level: Int): List<Quiz>
}
