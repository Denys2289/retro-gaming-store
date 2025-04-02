package com.example.retrogamingstore.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.retrogamingstore.model.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category AND isDeleted = 0 ORDER BY name ASC")
    fun getProductsByCategory(category: String): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Long): Product

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' AND isDeleted = 0")
    fun searchProducts(query: String): LiveData<List<Product>>

    @Insert
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("UPDATE products SET isDeleted = 1 WHERE id = :productId")
    suspend fun softDelete(productId: Long)
}