package com.example.pocketwallet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1") suspend fun getByUsername(username: String): User?
}
