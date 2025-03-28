package com.example.retrogamingstore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.retrogamingstore.model.User
import com.example.retrogamingstore.model.UserRole

@Database(entities = [User::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "retro_gaming_database"
                ).build()
            }
            return instance!!
        }
    }
}

class Converters {
    @androidx.room.TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @androidx.room.TypeConverter
    fun toUserRole(roleName: String): UserRole {
        return UserRole.valueOf(roleName)
    }
}