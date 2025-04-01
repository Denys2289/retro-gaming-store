package com.example.retrogamingstore.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrogamingstore.R
import com.example.retrogamingstore.databinding.FragmentCheckoutBinding
import com.example.retrogamingstore.ui.adapter.CheckoutItemAdapter
import com.example.retrogamingstore.ui.viewmodel.CheckoutViewModel

class CheckoutFragment : Fragment() {
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CheckoutViewModel
    private lateinit var adapter: CheckoutItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeData()
        setupListeners()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = CheckoutItemAdapter()
        binding.recyclerViewOrderItems.adapter = adapter
        binding.recyclerViewOrderItems.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeData() {
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        viewModel.totalAmount.observe(viewLifecycleOwner) { totalAmount ->
            binding.tvTotalAmount.text = "Загальна сума: ₴$totalAmount"
        }

        viewModel.orderStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                CheckoutViewModel.OrderStatus.IDLE -> {
                    binding.btnPlaceOrder.isEnabled = true
                    binding.btnPlaceOrder.text = "Оформити замовлення"
                }
                CheckoutViewModel.OrderStatus.PROCESSING -> {
                    binding.btnPlaceOrder.isEnabled = false
                    binding.btnPlaceOrder.text = "Обробка..."
                }
                CheckoutViewModel.OrderStatus.SUCCESS -> {
                    Toast.makeText(
                        requireContext(),
                        "Замовлення успішно оформлено!",
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().navigate(R.id.action_checkoutFragment_to_orderConfirmationFragment)
                }
                CheckoutViewModel.OrderStatus.ERROR -> {
                    binding.btnPlaceOrder.isEnabled = true
                    binding.btnPlaceOrder.text = "Оформити замовлення"
                    Toast.makeText(
                        requireContext(),
                        "Помилка при оформленні замовлення. Спробуйте знову.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPlaceOrder.setOnClickListener {
            if (validateForm()) {
                val address = binding.etAddress.text.toString()
                val phone = binding.etPhone.text.toString()
                val paymentMethod = when {
                    binding.rbCash.isChecked -> "CASH"
                    binding.rbCard.isChecked -> "CARD"
                    binding.rbOnline.isChecked -> "ONLINE"
                    else -> "CASH"
                }

                viewModel.placeOrder(address, phone, paymentMethod)
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val name = binding.etName.text.toString()
        val phone = binding.etPhone.text.toString()
        val address = binding.etAddress.text.toString()

        if (name.isEmpty()) {
            binding.tilName.error = "Введіть ім'я та прізвище"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (phone.isEmpty()) {
            binding.tilPhone.error = "Введіть номер телефону"
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        if (address.isEmpty()) {
            binding.tilAddress.error = "Введіть адресу доставки"
            isValid = false
        } else {
            binding.tilAddress.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}