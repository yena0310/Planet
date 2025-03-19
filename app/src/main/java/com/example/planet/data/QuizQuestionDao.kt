package com.example.planet.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface QuizQuestionDao {  // ✅ 인터페이스 (클래스 아님)
    @Query("SELECT * FROM quiz_question")
    suspend fun getAllQuestions(): List<QuizQuestionEntity>
}
