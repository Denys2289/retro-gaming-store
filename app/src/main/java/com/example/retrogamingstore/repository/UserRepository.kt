package com.example.retrogamingstore.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.retrogamingstore.database.UserDao
import com.example.retrogamingstore.model.User

class UserRepository(private val userDao: UserDao, context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    suspend fun getUserByUsernameAndPassword(username: String, password: String): User? {
        return userDao.findUser(username, password)
    }

    suspend fun getCurrentUserId(): Int? {
        return sharedPreferences.getInt("user_id", -1).takeIf { it != -1 }
    }

    fun saveCurrentUser(user: User) {
        sharedPreferences.edit()
            .putInt("user_id", user.id)
            .putString("username", user.username)
            .putInt("role", user.role)
            .apply()
    }
    suspend fun updateUserPassword(userId: Int, newPassword: String) {
        userDao.updateUserPassword(userId, newPassword)
    }



    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    suspend fun clearCurrentUser() {
        sharedPreferences.edit().clear().apply()
    }


    suspend fun getCurrentUser(): User? {
        val userId = getCurrentUserId() ?: return null
        return getUserById(userId)
    }

    suspend fun getUserByUsernameOrEmail(username: String, email: String): User? {
        return userDao.getUserByUsernameOrEmail(username, email)
    }

    suspend fun findUser(username: String, password: String): User? {
        return userDao.findUser(username, password)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun deleteUser(username: String) {
        userDao.deleteUser(username)
    }


}