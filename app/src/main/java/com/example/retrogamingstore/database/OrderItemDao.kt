package com.example.retrogamingstore.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.retrogamingstore.model.OrderItem
import com.example.retrogamingstore.model.Product

@Dao
interface OrderItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItem): Long

    @Update
    suspend fun updateOrderItem(orderItem: OrderItem)

    @Delete
    suspend fun deleteOrderItem(orderItem: OrderItem)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsByOrderId(orderId: Long): LiveData<List<OrderItem>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Long): Product

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getAllOrderItemsByOrderId(orderId: Long): List<OrderItem>

    // In your OrderItemDao interface
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsByOrderIdDirect(orderId: Long): List<OrderItem>
}