package com.example.planet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "school")
data class School(
    @PrimaryKey(autoGenerate = true) val schoolId: Int = 0,
    val name: String
)
