package com.example.retrogamingstore.ui.admin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.model.Order
import com.example.retrogamingstore.model.OrderItem
import com.example.retrogamingstore.model.OrderItemWithProduct
import kotlinx.coroutines.launch

class AdminOrdersViewModel(application: Application) : AndroidViewModel(application) {

    private val orderDao = AppDatabase.getDatabase(application).orderDao()
    private val orderItemDao = AppDatabase.getDatabase(application).orderItemDao()
    private val productDao = AppDatabase.getDatabase(application).productDao()

    // Всі замовлення
    val allOrders: LiveData<List<Order>> = orderDao.getAllOrders()

    // Отримання замовлення за ID
    fun getOrderById(orderId: Long): LiveData<Order> {
        val result = MutableLiveData<Order>()

        viewModelScope.launch {
            try {
                val order = orderDao.getOrderById(orderId)
                if (order != null) {
                    Log.d("AdminOrdersViewModel", "Order loaded: ${order.id}")
                    result.postValue(order)
                } else {
                    Log.e("AdminOrdersViewModel", "Order not found for ID: $orderId")
                }
            } catch (e: Exception) {
                Log.e("AdminOrdersViewModel", "Error loading order: ${e.message}", e)
            }
        }

        return result
    }

    // Отримання замовлення з деталями
    fun getOrderWithDetails(orderId: Long): LiveData<OrderWithDetailsAndProducts?> {
        val result = MutableLiveData<OrderWithDetailsAndProducts?>()

        viewModelScope.launch {
            try {
                // Get order
                val order = orderDao.getOrderById(orderId)

                if (order == null) {
                    Log.e("AdminOrdersViewModel", "Order not found for ID: $orderId")
                    result.postValue(null)
                    return@launch
                }

                // Отримуємо елементи замовлення
                val orderItemsWithProducts = mutableListOf<OrderItemWithProduct>()

                try {
                    // Fix: Get order items directly as a List, not as LiveData
                    val orderItems = orderItemDao.getOrderItemsByOrderIdDirect(orderId)

                    // Process each order item
                    for (orderItem in orderItems) {
                        processOrderItem(orderItem, orderItemsWithProducts)
                    }

                } catch (e: Exception) {
                    Log.e("AdminOrdersViewModel", "Error getting order items: ${e.message}", e)

                    // Alternative approach if the first method fails
                    try {
                        val allOrderItems = orderItemDao.getAllOrderItemsByOrderId(orderId)
                        if (allOrderItems != null) {
                            for (item in allOrderItems) {
                                processOrderItem(item, orderItemsWithProducts)
                            }
                        }
                    } catch (nestedE: Exception) {
                        Log.e("AdminOrdersViewModel", "Failed to retrieve order items with alternative method: ${nestedE.message}", nestedE)
                    }
                }

                Log.d("AdminOrdersViewModel", "Successfully loaded order details with ${orderItemsWithProducts.size} products")
                result.postValue(OrderWithDetailsAndProducts(order, orderItemsWithProducts))
            } catch (e: Exception) {
                Log.e("AdminOrdersViewModel", "Error loading order details: ${e.message}", e)
                result.postValue(null)
            }
        }

        return result
    }

    // Допоміжний метод для обробки одного елемента замовлення
    private suspend fun processOrderItem(orderItem: OrderItem, resultList: MutableList<OrderItemWithProduct>) {
        try {
            val product = productDao.getProductById(orderItem.productId)
            if (product != null) {
                resultList.add(OrderItemWithProduct(orderItem, product))
            } else {
                Log.w("AdminOrdersViewModel", "Product not found for product ID: ${orderItem.productId}")
            }
        } catch (e: Exception) {
            Log.e("AdminOrdersViewModel", "Error loading product: ${e.message}", e)
        }
    }

    // Оновлення замовлення
    fun updateOrder(order: Order) {
        viewModelScope.launch {
            try {
                orderDao.updateOrder(order)
                Log.d("AdminOrdersViewModel", "Order updated: ${order.id}")
            } catch (e: Exception) {
                Log.e("AdminOrdersViewModel", "Error updating order: ${e.message}", e)
            }
        }
    }

    // Клас для представлення замовлення з деталями та інформацією про товари
    data class OrderWithDetailsAndProducts(
        val order: Order,
        val orderItemsWithProducts: List<OrderItemWithProduct>
    )
}