package com.example.planet.data.dao

import androidx.room.*
import com.example.planet.data.entity.School

@Dao
interface SchoolDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(school: School)

    @Query("SELECT * FROM school")
    suspend fun getAll(): List<School>
}
