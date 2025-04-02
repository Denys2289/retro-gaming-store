package com.example.retrogamingstore.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrogamingstore.databinding.FragmentAdminOrdersListBinding
import com.example.retrogamingstore.model.Order
import com.example.retrogamingstore.ui.adapter.OrderAdapter
import com.example.retrogamingstore.ui.adapter.OrderAdminAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class AdminOrdersListFragment : Fragment() {

    private var _binding: FragmentAdminOrdersListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AdminOrdersViewModel
    private lateinit var orderAdapter: OrderAdminAdapter  // було OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminOrdersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AdminOrdersViewModel::class.java)

        setupRecyclerView()
        setupBackButton()
        observeOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdminAdapter { order ->
            // Перехід до деталей замовлення
            val action = AdminOrdersListFragmentDirections.actionAdminOrdersListFragmentToAdminOrderDetailsFragment(order.id)
            findNavController().navigate(action)
        }

        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeOrders() {
        viewModel.allOrders.observe(viewLifecycleOwner) { orders ->
            orderAdapter.submitList(orders)

            // Показуємо повідомлення, якщо список порожній
            if (orders.isEmpty()) {
                binding.emptyOrdersMessage.visibility = View.VISIBLE
            } else {
                binding.emptyOrdersMessage.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}