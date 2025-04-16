package com.example.planet.data.dao

import androidx.room.*
import com.example.planet.data.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user WHERE userId = :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM user ORDER BY ranking ASC")
    suspend fun getAllUsers(): List<User>
}
