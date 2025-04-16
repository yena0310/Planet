package com.example.planet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz")
data class Quiz(
    @PrimaryKey(autoGenerate = true) val quizId: Int = 0,
    val level: Int,
    val type: Int, // 1: OX, 2: 객관식, 3: 주관식, 4: 연결
    val question: String,
    val answer: String,
    val explanation: String
)
