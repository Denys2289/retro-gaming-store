package com.example.retrogamingstore.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrogamingstore.model.CartItem
import com.example.retrogamingstore.model.Product
import com.example.retrogamingstore.repository.CartRepository
import com.example.retrogamingstore.repository.UserRepository
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _cartProducts = MutableLiveData<List<Product>>()
    val cartProducts: LiveData<List<Product>> = _cartProducts

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private var currentUserId: Long = -1

    init {
        loadCurrentUserId()
    }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                currentUserId = userId.toLong()
                loadCartData()
            }
        }
    }

    // Необхідно викликати цей метод при зміні користувача
    fun onUserChanged() {
        loadCurrentUserId()
    }

    private fun loadCartData() {
        viewModelScope.launch {
            try {
                val cartWithDetails = cartRepository.getCartItemsWithProductDetails(currentUserId)

                // Оновлюємо список продуктів
                val products = cartWithDetails.map { it.product }
                _cartProducts.postValue(products)

                // Оновлюємо список елементів корзини
                val items = cartWithDetails.map { it.cartItem }
                _cartItems.postValue(items)

                // Розраховуємо загальну суму
                val total = cartWithDetails.sumOf { it.product.price * it.quantity }
                _totalPrice.postValue(total)
            } catch (e: Exception) {
                // Обробка помилок, якщо потрібно
                e.printStackTrace()
            }
        }
    }

    fun addToCart(productId: Long, quantity: Int = 1) {
        viewModelScope.launch {
            if (currentUserId != -1L) {
                cartRepository.addToCart(productId, currentUserId, quantity)
                loadCartData()
            }
        }
    }

    fun removeProductFromCart(productId: Long) {
        viewModelScope.launch {
            if (currentUserId != -1L) {
                val cartItem = cartRepository.getCartItemByProductId(productId, currentUserId)
                if (cartItem != null) {
                    cartRepository.removeFromCart(cartItem.id)
                    loadCartData()
                }
            }
        }
    }

    fun incrementQuantity(productId: Long) {
        viewModelScope.launch {
            if (currentUserId != -1L) {
                cartRepository.incrementQuantity(productId, currentUserId)
                loadCartData()
            }
        }
    }

    fun decrementQuantity(productId: Long) {
        viewModelScope.launch {
            if (currentUserId != -1L) {
                cartRepository.decrementQuantity(productId, currentUserId)
                loadCartData()
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            if (currentUserId != -1L) {
                cartRepository.clearCart(currentUserId)
                loadCartData()
            }
        }
    }
}