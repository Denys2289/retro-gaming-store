package com.example.retrogamingstore.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Relation
import java.util.Date

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long, // ID користувача, який зробив замовлення
    val orderDate: Date, // Дата замовлення
    val totalAmount: Double, // Загальна сума замовлення
    val status: String, // Статус замовлення: "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"
    val deliveryAddress: String, // Адреса доставки
    val contactPhone: String, // Контактний телефон
    val paymentMethod: String // Метод оплати: "CASH", "CARD", "ONLINE"
)