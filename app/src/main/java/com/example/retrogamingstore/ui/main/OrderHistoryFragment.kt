package com.example.retrogamingstore.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.databinding.FragmentOrderHistoryBinding
import com.example.retrogamingstore.repository.OrderRepository
import com.example.retrogamingstore.repository.UserRepository
import com.example.retrogamingstore.ui.adapter.OrderAdapter
import kotlinx.coroutines.launch

class OrderHistoryFragment : Fragment() {
    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderRepository: OrderRepository
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        orderRepository = OrderRepository(database.orderDao())
        userRepository = UserRepository(database.userDao(), requireContext())

        setupRecyclerView()
        loadOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter { order ->
            // При натисканні на замовлення можна додати дію відкриття деталей
            // Наприклад, будемо виводити тост з ID замовлення
            Toast.makeText(requireContext(), "Замовлення #${order.id}", Toast.LENGTH_SHORT).show()

            // Тут можна додати навігацію до деталей замовлення
            // findNavController().navigate(...)
        }

        binding.recyclerOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    private fun loadOrders() {
        lifecycleScope.launch {
            val currentUserId = userRepository.getCurrentUserId()

            if (currentUserId != null) {
                orderRepository.getOrdersByUser(currentUserId.toLong()).observe(viewLifecycleOwner) { orders ->
                    if (orders.isNotEmpty()) {
                        orderAdapter.submitList(orders)
                        binding.textNoOrders.visibility = View.GONE
                        binding.recyclerOrders.visibility = View.VISIBLE
                    } else {
                        binding.textNoOrders.visibility = View.VISIBLE
                        binding.recyclerOrders.visibility = View.GONE
                    }
                }
            } else {
                binding.textNoOrders.visibility = View.VISIBLE
                binding.recyclerOrders.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}