package com.example.retrogamingstore.model

import androidx.room.Embedded
import androidx.room.Relation

data class OrderItemWithProduct(
    @Embedded val orderItem: OrderItem,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product
)