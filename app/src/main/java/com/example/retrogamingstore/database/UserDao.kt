package com.example.retrogamingstore.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.retrogamingstore.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun findUser(username: String, password: String): User?


    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUser(username: String)
}
