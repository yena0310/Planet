package com.example.planet.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "class",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["schoolId"])]
)
data class Class(
    @PrimaryKey(autoGenerate = true) val classId: Int = 0,
    val schoolId: Int,
    val grade: Int,
    val classNum: Int
)
