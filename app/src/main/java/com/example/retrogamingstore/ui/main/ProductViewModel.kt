package com.example.retrogamingstore.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.database.ProductDao
import com.example.retrogamingstore.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Визначення опцій сортування
enum class SortOrder {
    PRICE_ASC,  // За зростанням ціни
    PRICE_DESC, // За спаданням ціни
    NEWEST,     // Спочатку новіші (за id, оскільки у моделі немає поля дати)
    OLDEST      // Спочатку старіші (за id, оскільки у моделі немає поля дати)
}

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    // Доступ до DAO
    private val productDao: ProductDao = AppDatabase.getDatabase(application).productDao()

    // Поточні значення фільтрів
    private var currentSearchQuery: String = ""
    private var currentSortOrder: SortOrder? = null

    // LiveData для результатів пошуку
    private val _searchResults = MediatorLiveData<List<Product>>()

    // Результат для відображення в UI
    private val _products = MediatorLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    // Джерело даних (змінюється в залежності від пошукового запиту)
    private var productsSource: LiveData<List<Product>> = productDao.getAllProducts()

    init {
        // Спочатку відображаємо всі продукти
        _products.addSource(productsSource) { products ->
            _products.value = applySorting(products, currentSortOrder)
        }
    }

    fun setSearchQuery(query: String) {
        // Зберігаємо запит для подальшого використання
        currentSearchQuery = query

        // Видаляємо старе джерело даних
        _products.removeSource(productsSource)

        // Оновлюємо джерело даних відповідно до запиту
        productsSource = if (query.isBlank()) {
            productDao.getAllProducts()
        } else {
            productDao.searchProducts(query)
        }

        // Додаємо нове джерело та застосовуємо сортування
        _products.addSource(productsSource) { products ->
            _products.value = applySorting(products, currentSortOrder)
        }
    }

    fun setSortOrder(sortOrder: SortOrder?) {
        currentSortOrder = sortOrder

        // Оновлюємо дані з новим сортуванням
        _products.value = applySorting(_products.value ?: emptyList(), sortOrder)
    }

    fun getCurrentSortOrder(): SortOrder? {
        return currentSortOrder
    }

    fun applyFilters() {
        // Вже виконано через реактивні зміни при зміні searchQuery та sortOrder
        // Можна використовувати для додаткової логіки фільтрації у майбутньому
    }

    fun resetFilters() {
        currentSearchQuery = ""
        currentSortOrder = null

        // Видаляємо старе джерело даних
        _products.removeSource(productsSource)

        // Скидаємо до дефолтного джерела
        productsSource = productDao.getAllProducts()

        // Додаємо нове джерело без сортування
        _products.addSource(productsSource) { products ->
            _products.value = products
        }
    }

    // Приватний метод для сортування списку продуктів
    private fun applySorting(products: List<Product>, sortOrder: SortOrder?): List<Product> {
        return when (sortOrder) {
            SortOrder.PRICE_ASC -> products.sortedBy { it.price }
            SortOrder.PRICE_DESC -> products.sortedByDescending { it.price }
            SortOrder.NEWEST -> products.sortedByDescending { it.id } // Використовуємо ID як заміну даті
            SortOrder.OLDEST -> products.sortedBy { it.id } // Використовуємо ID як заміну даті
            null -> products // Без сортування
        }
    }
}