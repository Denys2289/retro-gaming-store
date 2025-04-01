package com.example.retrogamingstore.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.retrogamingstore.databinding.FragmentListBinding
import com.example.retrogamingstore.model.Product
import com.example.retrogamingstore.ui.adapter.ProductAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.retrogamingstore.R
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.retrogamingstore.ui.viewmodel.CartViewModel
import java.io.File

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewModel: ProductViewModel
    private lateinit var cartViewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeData()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity())[ProductViewModel::class.java]
        cartViewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            showProductDetails(product)
        }

        binding.recyclerViewProducts.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun observeData() {
        viewModel.allProducts.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
        }
    }

    private fun showProductDetails(product: Product) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_product_details, null)

        // Ініціалізація елементів у модальному вікні
        val imageView = view.findViewById<ImageView>(R.id.product_detail_image)
        val nameTextView = view.findViewById<TextView>(R.id.product_detail_name)
        val priceTextView = view.findViewById<TextView>(R.id.product_detail_price)
        val descriptionTextView = view.findViewById<TextView>(R.id.product_detail_description)
        val categoryTextView = view.findViewById<TextView>(R.id.product_detail_category)
        val conditionTextView = view.findViewById<TextView>(R.id.product_detail_condition)
        val addToCartButton = view.findViewById<Button>(R.id.btn_add_to_cart)

        // Заповнення даними
        nameTextView.text = product.name
        priceTextView.text = "₴${product.price}"
        descriptionTextView.text = product.description
        categoryTextView.text = "Категорія: ${product.category}"
        conditionTextView.text = "Стан: ${product.condition}"

        // Завантаження зображення
        if (product.imagePath.isNotEmpty()) {
            val imageFile = File(product.imagePath)
            if (imageFile.exists()) {
                Glide.with(requireContext())
                    .load(imageFile)
                    .placeholder(R.drawable.placeholder_game)
                    .error(R.drawable.placeholder_game)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.placeholder_game)
            }
        } else {
            imageView.setImageResource(R.drawable.placeholder_game)
        }

        // Обробка натискання на кнопку "Додати в кошик"
        addToCartButton.setOnClickListener {
            cartViewModel.addToCart(product)
            Toast.makeText(requireContext(), "Товар додано в кошик", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}