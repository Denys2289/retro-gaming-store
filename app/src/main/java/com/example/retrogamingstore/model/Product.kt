package com.example.retrogamingstore.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double,
    val imagePath: String, // Шлях до зображення у внутрішньому сховищі
    val category: String,
    val condition: String, // Наприклад: "New", "Used - Good", "Used - Fair"
    val inStock: Boolean = true,
    val quantity: Int = 1,
    val releaseYear: Int? = null,
    val manufacturer: String? = null
) : Parcelable