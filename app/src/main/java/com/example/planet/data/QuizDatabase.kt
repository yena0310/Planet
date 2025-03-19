package com.example.planet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QuizQuestionEntity::class], version = 1)
abstract class QuizDatabase : RoomDatabase() {  // ✅ 클래스!

    abstract fun quizQuestionDao(): QuizQuestionDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz.db"
                )
                    .createFromAsset("databases/quiz.db") // assets에 넣을 경우
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
