package com.example.retrogamingstore.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.model.CartItem
import com.example.retrogamingstore.model.Product
import com.example.retrogamingstore.repository.CartRepository
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CartRepository
    private val currentUserId = 1L // Або отримуйте його з акаунту користувача
    val cartProducts = MediatorLiveData<List<Product>>()
    val totalPrice = MediatorLiveData<Double>()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CartRepository(database.cartDao(), database.productDao())

        loadCartData()
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                repository.addToCart(product.id, currentUserId)
                loadCartData()
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error adding product to cart", e)
            }
        }
    }

    private fun loadCartData() {
        viewModelScope.launch {
            try {
                // Отримуємо дані з кошика
                val cartItemsWithDetails = repository.getCartItemsWithDetails(currentUserId)

                // Перетворюємо на список Product з кількістю
                val products = cartItemsWithDetails.map { cartProduct ->
                    cartProduct.product.copy(quantity = cartProduct.quantity)
                }

                // Оновлюємо LiveData
                cartProducts.postValue(products)

                // Розраховуємо загальну суму
                val total = products.sumOf { it.price * it.quantity }
                totalPrice.postValue(total)
            } catch (e: Exception) {
                // Обробка помилок
                Log.e("CartViewModel", "Error loading cart data", e)
            }
        }
    }

    fun incrementQuantity(productId: Long) {
        viewModelScope.launch {
            try {
                // Оновлюємо кількість в базі даних
                repository.incrementQuantity(productId, currentUserId)

                // Перезавантажуємо дані кошика
                loadCartData()
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error incrementing quantity", e)
            }
        }
    }

    fun decrementQuantity(productId: Long) {
        viewModelScope.launch {
            try {
                // Оновлюємо кількість в базі даних
                repository.decrementQuantity(productId, currentUserId)

                // Перезавантажуємо дані кошика
                loadCartData()
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error decrementing quantity", e)
            }
        }
    }

    fun removeFromCart(product: Product) {
        viewModelScope.launch {
            try {
                // Отримуємо CartItem за productId
                val cartItem = repository.getCartItemByProductId(product.id, currentUserId)

                // Видаляємо товар з кошика
                cartItem?.let { repository.removeFromCart(it.id) }

                // Перезавантажуємо дані кошика
                loadCartData()
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error removing product from cart", e)
            }
        }
    }

    fun removeProductFromCart(productId: Long) {
        viewModelScope.launch {
            try {
                val cartItem = repository.getCartItemByProductId(productId, currentUserId)
                cartItem?.let {
                    repository.removeFromCart(it.id)
                    loadCartData()
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error removing product from cart", e)
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                repository.clearCart(currentUserId)
                loadCartData()
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error clearing cart", e)
            }
        }
    }
}