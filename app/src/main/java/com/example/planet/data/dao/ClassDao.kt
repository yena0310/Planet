package com.example.planet.data.dao

import androidx.room.*
import com.example.planet.data.entity.Class

@Dao
interface ClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(classEntity: Class)

    @Query("SELECT * FROM class WHERE schoolId = :schoolId")
    suspend fun getClassesBySchool(schoolId: Int): List<Class>
}
