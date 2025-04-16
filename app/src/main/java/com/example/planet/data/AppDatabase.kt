package com.example.planet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.planet.data.dao.ClassDao
import com.example.planet.data.dao.HistoryDao
import com.example.planet.data.dao.QuizDao
import com.example.planet.data.dao.SchoolDao
import com.example.planet.data.dao.UserDao
import com.example.planet.data.entity.User

@Database(
    entities = [User::class, Quiz::class, School::class, ClassEntity::class, History::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun quizDao(): QuizDao
    abstract fun schoolDao(): SchoolDao
    abstract fun classDao(): ClassDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "planet_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
