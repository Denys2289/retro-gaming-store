package com.example.retrogamingstore.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.retrogamingstore.model.Order
import com.example.retrogamingstore.model.OrderItem
import com.example.retrogamingstore.model.OrderItemWithProduct
import com.example.retrogamingstore.model.OrderWithDetails

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(orderItems: List<OrderItem>)

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): LiveData<List<Order>>


    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderWithItems(orderId: Long): LiveData<
            OrderWithDetails>
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    fun getOrdersByUser(userId: Long): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Long): Order
    // Якщо потрібен окремий метод для отримання всіх елементів

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsWithProducts(orderId: Long): List<OrderItemWithProduct>
    // Має бути так:
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsByOrderId(orderId: Long): List<OrderItem>

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)


}