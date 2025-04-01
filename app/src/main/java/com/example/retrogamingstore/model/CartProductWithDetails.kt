package com.example.retrogamingstore.model

// Клас для відображення інформації про товар у кошику з деталями
data class CartProductWithDetails(
    val cartItem: CartItem,
    val product: Product,
    val quantity: Int
)