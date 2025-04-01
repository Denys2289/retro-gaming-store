package com.example.retrogamingstore.repository

import androidx.lifecycle.LiveData
import com.example.retrogamingstore.database.OrderDao
import com.example.retrogamingstore.model.Order
import com.example.retrogamingstore.model.OrderItem

class OrderRepository(private val orderDao: OrderDao) {

    suspend fun insertOrder(order: Order): Long {
        return orderDao.insertOrder(order)
    }

    suspend fun insertOrderItems(orderItems: List<OrderItem>) {
        orderDao.insertOrderItems(orderItems)
    }

    fun getOrdersByUser(userId: Long): LiveData<List<Order>> {
        return orderDao.getOrdersByUser(userId)
    }

    suspend fun getOrderById(orderId: Long): Order {
        return orderDao.getOrderById(orderId)
    }

    suspend fun getOrderItemsByOrderId(orderId: Long): List<OrderItem> {
        return orderDao.getOrderItemsByOrderId(orderId)
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) {
        orderDao.updateOrderStatus(orderId, status)
    }
}