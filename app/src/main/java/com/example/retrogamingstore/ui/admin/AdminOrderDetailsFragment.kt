package com.example.retrogamingstore.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrogamingstore.R
import com.example.retrogamingstore.databinding.FragmentAdminOrderDetailsBinding
import com.example.retrogamingstore.model.Order
import com.example.retrogamingstore.ui.adapter.OrderItemAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class AdminOrderDetailsFragment : Fragment() {

    private var _binding: FragmentAdminOrderDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AdminOrdersViewModel
    private lateinit var orderItemAdapter: OrderItemAdapter
    private val args: AdminOrderDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminOrdersViewModel::class.java)

        setupToolbar()
        setupStatusDropdown()
        setupPaymentMethodDropdown()
        setupRecyclerView()
        setupSaveButton()
        loadOrderDetails()
    }

    private fun loadOrderDetails() {
        val orderId = args.orderId
        Log.d("AdminOrderDetails", "Loading order with ID: $orderId")

        // Спочатку завантажимо базову інформацію про замовлення
        viewModel.getOrderById(orderId).observe(viewLifecycleOwner) { order ->
            if (order != null) {
                Log.d("AdminOrderDetails", "Order data loaded: ${order.id}")
                displayOrderDetails(order)
            } else {
                Log.e("AdminOrderDetails", "Order not found!")
                Toast.makeText(context, "Замовлення не знайдено", Toast.LENGTH_SHORT).show()
            }
        }

        // Потім завантажимо детальну інформацію включно з товарами
        viewModel.getOrderWithDetails(orderId).observe(viewLifecycleOwner) { orderWithDetailsAndProducts ->
            if (orderWithDetailsAndProducts != null) {
                Log.d("AdminOrderDetails", "Order details loaded with ${orderWithDetailsAndProducts.orderItemsWithProducts.size} items")
                // Позначаємо замовлення ще раз на випадок проблем з першим завантаженням
                displayOrderDetails(orderWithDetailsAndProducts.order)
                orderItemAdapter.submitList(orderWithDetailsAndProducts.orderItemsWithProducts)
            } else {
                Log.e("AdminOrderDetails", "Failed to load order details!")
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupStatusDropdown() {
        val statusOptions = arrayOf(
            "PENDING",
            "CONFIRMED",
            "SHIPPED",
            "DELIVERED",
            "CANCELLED"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            statusOptions
        )
        binding.dropdownStatus.setAdapter(adapter)
    }

    private fun setupPaymentMethodDropdown() {
        val paymentOptions = arrayOf(
            "CASH",
            "CARD",
            "ONLINE"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            paymentOptions
        )
        binding.dropdownPayment.setAdapter(adapter)
    }

    private fun setupRecyclerView() {
        orderItemAdapter = OrderItemAdapter()
        binding.recyclerViewOrderItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderItemAdapter
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveChanges.setOnClickListener {
            saveOrderChanges()
        }
    }

    private fun displayOrderDetails(order: Order) {
        binding.tvOrderId.text = "#${order.id}"
        binding.tvUserId.text = order.userId.toString()

        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(order.orderDate)

        binding.tvTotal.text = String.format("%.2f грн", order.totalAmount)
        binding.dropdownStatus.setText(order.status, false)
        binding.etAddress.setText(order.deliveryAddress)
        binding.etPhone.setText(order.contactPhone)
        binding.dropdownPayment.setText(order.paymentMethod, false)
    }

    private fun saveOrderChanges() {
        val orderId = args.orderId
        viewModel.getOrderById(orderId).observe(viewLifecycleOwner) { order ->
            if (order != null) {
                // Створюємо оновлений об'єкт замовлення з новими даними
                val updatedOrder = order.copy(
                    status = binding.dropdownStatus.text.toString(),
                    deliveryAddress = binding.etAddress.text.toString(),
                    contactPhone = binding.etPhone.text.toString(),
                    paymentMethod = binding.dropdownPayment.text.toString()
                )

                viewModel.updateOrder(updatedOrder)
                Toast.makeText(requireContext(), "Замовлення оновлено", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}