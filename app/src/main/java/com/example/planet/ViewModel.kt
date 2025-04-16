package com.example.planet

import android.content.Context
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import com.example.planet.data.AppDatabase
import com.example.planet.data.User

object DatabaseInitializer {
    fun initialize(context: Context) {
        val db = AppDatabase.getDatabase(context)
        val userDao = db.userDao()

        LaunchedEffect(Unit) {
            userDao.insert(
                User(
                    classId = 1,
                    name = "홍길동",
                    score = 100,
                    ranking = 1,
                    profilePhotoPath = ""
                )
            )
            val users = userDao.getAll()
            Log.d("DB", "사용자 목록: $users")
        }
    }
}
