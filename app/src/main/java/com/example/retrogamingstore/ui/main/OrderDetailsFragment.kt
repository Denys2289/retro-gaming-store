package com.example.retrogamingstore.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.databinding.FragmentOrderDetailsBinding
import com.example.retrogamingstore.model.Order
import com.example.retrogamingstore.model.OrderItem
import com.example.retrogamingstore.model.Product
import com.example.retrogamingstore.repository.OrderRepository
import com.example.retrogamingstore.ui.adapter.OrderItemAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailsFragment : Fragment() {
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderRepository: OrderRepository
    private lateinit var orderItemAdapter: OrderItemAdapter
    private var orderId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderId = it.getLong("order_id", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        orderRepository = OrderRepository(database.orderDao())

        setupRecyclerView()
        loadOrderDetails()
    }

    private fun setupRecyclerView() {
        orderItemAdapter = OrderItemAdapter()
        binding.recyclerOrderItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderItemAdapter
        }
    }

    private fun loadOrderDetails() {
        if (orderId == -1L) {
            // Якщо ID замовлення недійсний, повертаємось назад
            requireActivity().onBackPressed()
            return
        }

        lifecycleScope.launch {
            // Отримуємо дані про замовлення та його елементи
            val order = orderRepository.getOrderById(orderId)
            val orderItems = orderRepository.getOrderItemsWithProducts(orderId)

            // Відображаємо основну інформацію про замовлення
            displayOrderInfo(order)

            // Відображаємо елементи замовлення
            orderItemAdapter.submitList(orderItems)
        }
    }

    private fun displayOrderInfo(order: Order) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        binding.textOrderId.text = "Замовлення #${order.id}"
        binding.textOrderDate.text = "Дата: ${dateFormat.format(order.orderDate)}"

        val statusText = when(order.status) {
            "PENDING" -> "Очікує підтвердження"
            "CONFIRMED" -> "Підтверджено"
            "SHIPPED" -> "Відправлено"
            "DELIVERED" -> "Доставлено"
            "CANCELLED" -> "Скасовано"
            else -> order.status
        }
        binding.textOrderStatus.text = "Статус: $statusText"

        binding.textTotalAmount.text = "Загальна сума: ${order.totalAmount} грн"
        binding.textDeliveryAddress.text = "Адреса доставки: ${order.deliveryAddress}"
        binding.textContactPhone.text = "Контактний телефон: ${order.contactPhone}"
        binding.textPaymentMethod.text = "Спосіб оплати: ${
            when(order.paymentMethod) {
                "CASH" -> "Готівка"
                "CARD" -> "Картка"
                "ONLINE" -> "Онлайн-оплата"
                else -> order.paymentMethod
            }
        }"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}