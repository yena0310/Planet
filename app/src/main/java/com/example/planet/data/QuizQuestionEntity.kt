package com.example.planet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_question")
data class QuizQuestionEntity(  // ✅ 클래스!
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val content: String,
    val category: Int,
    val answer: String,
    val explanation: String
)
