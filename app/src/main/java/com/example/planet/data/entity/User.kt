package com.example.planet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val classId: Int,
    val name: String,
    val score: Int,
    val ranking: Int,
    val profilePhotoPath: String // BLOB 대신 경로 저장
)
