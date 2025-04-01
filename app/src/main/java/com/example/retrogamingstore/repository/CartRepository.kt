package com.example.retrogamingstore.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.retrogamingstore.database.CartDao
import com.example.retrogamingstore.database.ProductDao
import com.example.retrogamingstore.model.CartItem
import com.example.retrogamingstore.model.CartProductWithDetails
import com.example.retrogamingstore.model.Product

class CartRepository(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) {
    suspend fun addToCart(productId: Long, userId: Long, quantity: Int = 1) {
        // Перевіряємо, чи товар вже є в кошику
        val existingItem = cartDao.getCartItemByProductAndUser(productId, userId)

        if (existingItem != null) {
            // Якщо товар вже є в кошику, збільшуємо кількість
            cartDao.updateQuantity(existingItem.id, existingItem.quantity + quantity)
        } else {
            // Якщо товару немає в кошику, додаємо його
            val cartItem = CartItem(
                productId = productId,
                quantity = quantity,
                userId = userId
            )
            cartDao.insert(cartItem)
        }
    }

    suspend fun updateQuantity(cartItemId: Long, quantity: Int) {
        cartDao.updateQuantity(cartItemId, quantity)
    }

    suspend fun removeFromCart(cartItemId: Long) {
        cartDao.delete(cartItemId)
    }

    suspend fun clearCart(userId: Long) {
        cartDao.clearCart(userId)
    }

    fun getCartItemsByUser(userId: Long): LiveData<List<CartItem>> {
        return cartDao.getCartItemsByUser(userId)
    }

    // Метод для отримання повної інформації про товари в кошику
    suspend fun getCartItemsWithProductDetails(userId: Long): List<CartProductWithDetails> {
        val cartItems = cartDao.getCartItemsByUserNonLive(userId)
        return cartItems.map { item ->
            val product = productDao.getProductById(item.productId)
            if (product != null) {
                CartProductWithDetails(
                    cartItem = item,
                    product = product,
                    quantity = item.quantity
                )
            } else {
                throw IllegalStateException("Продукт не знайдено для id: ${item.productId}")
            }
        }
    }


    // Оновіть метод, щоб отримувати Product, а не LiveData
    suspend fun getCartItemsWithDetails(userId: Long): List<CartProductWithDetails> {
        val cartItems = cartDao.getCartItemsByUserNonLive(userId)
        return cartItems.map { item ->
            // Отримуємо продукт без LiveData
            val product = productDao.getProductById(item.productId)
            if (product != null) {
                CartProductWithDetails(
                    cartItem = item,
                    product = product,
                    quantity = item.quantity
                )
            } else {
                throw IllegalStateException("Продукт не знайдено для id: ${item.productId}")
            }
        }
    }


}