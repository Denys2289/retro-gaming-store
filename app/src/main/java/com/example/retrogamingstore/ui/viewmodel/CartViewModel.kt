package com.example.retrogamingstore.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.model.CartItem
import com.example.retrogamingstore.model.Product
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val cartDao = AppDatabase.getDatabase(application).cartDao()
    private val currentUserId: Long = 1 // Замініть на отримання ID поточного користувача

    // Отримання продуктів у кошику
    val cartProducts: LiveData<List<Product>> = cartDao.getCartWithProducts(currentUserId)
    val totalPrice: LiveData<Double> = cartDao.getTotalCartPrice(currentUserId)

    // Додавання товару в кошик
    fun addToCart(product: Product) {
        viewModelScope.launch {
            // Перевіряємо, чи товар вже є в кошику
            val isInCart = cartDao.isProductInCart(product.id, currentUserId)
            if (isInCart > 0) {
                // Якщо товар вже є в кошику, збільшуємо кількість
                cartDao.incrementQuantity(product.id, currentUserId)
            } else {
                // Інакше додаємо новий запис
                val cartItem = CartItem(
                    productId = product.id,
                    quantity = 1,
                    userId = currentUserId
                )
                cartDao.insertCartItem(cartItem)
            }
        }
    }

    // Видалення товару з кошика
    fun removeFromCart(product: Product) {
        viewModelScope.launch {
            val cartItem = CartItem(
                productId = product.id,
                quantity = 1,
                userId = currentUserId
            )
            cartDao.deleteCartItem(cartItem)
        }
    }

    // Збільшення кількості товару в кошику
    fun incrementQuantity(productId: Long) {
        viewModelScope.launch {
            cartDao.incrementQuantity(productId, currentUserId)
        }
    }

    // Зменшення кількості товару в кошику
    fun decrementQuantity(productId: Long) {
        viewModelScope.launch {
            cartDao.decrementQuantity(productId, currentUserId)
        }
    }

    // Очищення кошика
    fun clearCart() {
        viewModelScope.launch {
            cartDao.clearCart(currentUserId)
        }
    }
}