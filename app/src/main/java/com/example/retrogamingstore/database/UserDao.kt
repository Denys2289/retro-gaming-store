package com.example.retrogamingstore.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.retrogamingstore.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun findUser(username: String, password: String): User?
}