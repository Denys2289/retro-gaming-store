package com.example.retrogamingstore.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.model.CartProductWithDetails
import com.example.retrogamingstore.model.Order
import com.example.retrogamingstore.model.OrderItem
import com.example.retrogamingstore.repository.CartRepository
import com.example.retrogamingstore.repository.OrderRepository
import com.example.retrogamingstore.repository.UserRepository
import kotlinx.coroutines.launch
import java.util.Date

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val orderRepository = OrderRepository(database.orderDao())
    private val cartRepository = CartRepository(database.cartDao(), database.productDao())
    private val userRepository = UserRepository(database.userDao(), application.applicationContext)  // Pass the application context here

    private val _orderStatus = MutableLiveData<OrderStatus>()
    val orderStatus: LiveData<OrderStatus> get() = _orderStatus

    private val _cartItems = MutableLiveData<List<CartProductWithDetails>>()
    val cartItems: LiveData<List<CartProductWithDetails>> get() = _cartItems

    private val _totalAmount = MutableLiveData<Double>()
    val totalAmount: LiveData<Double> get() = _totalAmount

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserId()?.toLong() ?: return@launch
            val items = cartRepository.getCartItemsWithDetails(currentUserId)
            _cartItems.value = items
            calculateTotal(items)
        }
    }

    private fun calculateTotal(items: List<CartProductWithDetails>) {
        var total = 0.0
        items.forEach { cartProduct ->
            total += cartProduct.product.price * cartProduct.quantity
        }
        _totalAmount.value = total
    }

    fun placeOrder(deliveryAddress: String, contactPhone: String, paymentMethod: String) {
        viewModelScope.launch {
            try {
                _orderStatus.value = OrderStatus.PROCESSING

                val currentUserId = userRepository.getCurrentUserId()?.toLong() ?: return@launch
                val items = _cartItems.value ?: return@launch
                val totalAmount = _totalAmount.value ?: 0.0

                // Create the order
                val order = Order(
                    userId = currentUserId,
                    orderDate = Date(),
                    totalAmount = totalAmount,
                    status = "PENDING",
                    deliveryAddress = deliveryAddress,
                    contactPhone = contactPhone,
                    paymentMethod = paymentMethod
                )

                // Save the order and get its ID
                val orderId = orderRepository.insertOrder(order)

                // Create order items
                val orderItems = items.map { cartProduct ->
                    OrderItem(
                        orderId = orderId,
                        productId = cartProduct.product.id,
                        quantity = cartProduct.quantity,
                        priceAtOrder = cartProduct.product.price
                    )
                }

                // Save the order items
                orderRepository.insertOrderItems(orderItems)

                // Clear the cart
                cartRepository.clearCart(currentUserId)

                _orderStatus.value = OrderStatus.SUCCESS
            } catch (e: Exception) {
                _orderStatus.value = OrderStatus.ERROR
            }
        }
    }

    enum class OrderStatus {
        IDLE, PROCESSING, SUCCESS, ERROR
    }
}
