package com.example.retrogamingstore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2, // Оновлена версія бази даних
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

        // Міграція з версії 1 на версію 2: додаємо поле isDeleted до таблиці products
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Додаємо новий стовпець із значенням за замовчуванням 0 (false)
                database.execSQL("ALTER TABLE products ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "retro_gaming_db"
                )
                    .addMigrations(MIGRATION_1_2) // Додаємо міграцію
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}