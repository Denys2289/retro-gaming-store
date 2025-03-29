package com.example.retrogamingstore.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.retrogamingstore.model.CartItem
import com.example.retrogamingstore.model.Product

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem): Long

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Long)

    @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE productId = :productId AND userId = :userId")
    suspend fun incrementQuantity(productId: Long, userId: Long)

    @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE productId = :productId AND userId = :userId AND quantity > 1")
    suspend fun decrementQuantity(productId: Long, userId: Long)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItems(userId: Long): LiveData<List<CartItem>>

    @Query("SELECT COUNT(*) FROM cart_items WHERE productId = :productId AND userId = :userId")
    suspend fun isProductInCart(productId: Long, userId: Long): Int

    @Transaction
    @Query("SELECT p.* FROM products p INNER JOIN cart_items c ON p.id = c.productId WHERE c.userId = :userId")
    fun getCartWithProducts(userId: Long): LiveData<List<Product>>

    @Query("SELECT SUM(p.price * c.quantity) FROM products p INNER JOIN cart_items c ON p.id = c.productId WHERE c.userId = :userId")
    fun getTotalCartPrice(userId: Long): LiveData<Double>

    @Query("SELECT c.*, p.* FROM cart_items c INNER JOIN products p ON c.productId = p.id WHERE c.userId = :userId")
    fun getFullCartInfo(userId: Long): LiveData<Map<CartItem, Product>>
}