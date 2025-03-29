package com.example.retrogamingstore.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val productDao = AppDatabase.getDatabase(application).productDao()

    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()

    fun insertProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productDao.insert(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productDao.update(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productDao.delete(product)
        }
    }
}