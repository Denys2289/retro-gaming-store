package com.example.retrogamingstore.model

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithDetails(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val orderItems: List<OrderItem>
)