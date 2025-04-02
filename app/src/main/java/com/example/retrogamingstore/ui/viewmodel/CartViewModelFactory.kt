package com.example.retrogamingstore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.retrogamingstore.repository.CartRepository
import com.example.retrogamingstore.repository.UserRepository

class CartViewModelFactory(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(cartRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}