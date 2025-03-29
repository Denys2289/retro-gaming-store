package com.example.retrogamingstore.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.model.Product

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productDao = AppDatabase.getDatabase(application).productDao()

    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()

    fun getProductsByCategory(category: String): LiveData<List<Product>> {
        return productDao.getProductsByCategory(category)
    }
}
