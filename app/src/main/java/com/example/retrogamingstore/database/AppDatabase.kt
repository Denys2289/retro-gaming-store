package com.example.retrogamingstore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.retrogamingstore.database.CartDao
import com.example.retrogamingstore.database.OrderDao
import com.example.retrogamingstore.data.dao.OrderItemDao
import com.example.retrogamingstore.database.ProductDao
import com.example.retrogamingstore.database.UserDao
import com.example.retrogamingstore.model.CartItem
import com.example.retrogamingstore.model.Order
import com.example.retrogamingstore.model.OrderItem
import com.example.retrogamingstore.model.Product
import com.example.retrogamingstore.model.User
import com.example.retrogamingstore.utils.DateConverter

@Database(
    entities = [User::class, Product::class, CartItem::class, Order::class, OrderItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "retro_gaming_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}