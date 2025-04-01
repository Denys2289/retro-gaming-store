package com.example.retrogamingstore.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrogamingstore.R
import com.example.retrogamingstore.databinding.FragmentCartBinding
import com.example.retrogamingstore.ui.adapter.CartAdapter
import com.example.retrogamingstore.ui.viewmodel.CartViewModel

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeData()
        setupButtons()
    }

    private fun setupViewModel() {
        cartViewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncrement = { productId -> cartViewModel.incrementQuantity(productId) },
            onDecrement = { productId -> cartViewModel.decrementQuantity(productId) },
            onRemove = { product -> cartViewModel.removeFromCart(product) }
        )

        binding.recyclerViewCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeData() {
        cartViewModel.cartProducts.observe(viewLifecycleOwner) { products ->
            cartAdapter.submitList(products)
            updateEmptyView(products.isEmpty())
        }

        cartViewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            binding.textTotalPrice.text = "Загальна сума: ₴${totalPrice ?: 0.0}"
        }
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyCartView.visibility = View.VISIBLE
            binding.cartContent.visibility = View.GONE
        } else {
            binding.emptyCartView.visibility = View.GONE
            binding.cartContent.visibility = View.VISIBLE
        }
    }

    private fun setupButtons() {
        binding.btnClearCart.setOnClickListener {
            cartViewModel.clearCart()
        }

        binding.btnCheckout.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}