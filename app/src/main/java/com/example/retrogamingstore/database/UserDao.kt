package com.example.retrogamingstore.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.retrogamingstore.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username OR email = :email LIMIT 1")
    suspend fun getUserByUsernameOrEmail(username: String, email: String): User?

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun findUser(username: String, password: String): User?

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUser(username: String)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?
}
