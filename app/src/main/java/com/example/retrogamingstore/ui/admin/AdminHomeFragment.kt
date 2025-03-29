package com.example.retrogamingstore.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrogamingstore.R
import com.example.retrogamingstore.databinding.FragmentAdminHomeBinding
import com.example.retrogamingstore.model.Product
import com.example.retrogamingstore.ui.adapter.ProductAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class AdminHomeFragment : Fragment() {

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewModel: AdminViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)

        setupRecyclerView()
        setupAddButton()
        observeProducts()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            // Обробка кліку на товар (наприклад, редагування)
            Toast.makeText(requireContext(), "Обрано товар: ${product.name}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    private fun setupAddButton() {
        binding.btnAddProduct.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun observeProducts() {
        viewModel.allProducts.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)

            // Відображення або приховування підказки порожнього списку
            if (products.isEmpty()) {
                binding.emptyProductsMessage.visibility = View.VISIBLE
            } else {
                binding.emptyProductsMessage.visibility = View.GONE
            }
        }
    }

    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)

        // Налаштування випадаючих списків
        setupCategorySpinner(dialogView)
        setupConditionSpinner(dialogView)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        // Налаштування кнопок
        dialogView.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btn_add_product).setOnClickListener {
            // Отримання даних з полів вводу
            val name = dialogView.findViewById<TextInputEditText>(R.id.et_product_name).text.toString()
            val description = dialogView.findViewById<TextInputEditText>(R.id.et_product_description).text.toString()
            val priceText = dialogView.findViewById<TextInputEditText>(R.id.et_product_price).text.toString()
            val category = dialogView.findViewById<AutoCompleteTextView>(R.id.spinner_category).text.toString()
            val condition = dialogView.findViewById<AutoCompleteTextView>(R.id.spinner_condition).text.toString()
            val quantityText = dialogView.findViewById<TextInputEditText>(R.id.et_product_quantity).text.toString()
            val imageUrl = dialogView.findViewById<TextInputEditText>(R.id.et_product_image_url).text.toString()
            val yearText = dialogView.findViewById<TextInputEditText>(R.id.et_product_year).text.toString()
            val manufacturer = dialogView.findViewById<TextInputEditText>(R.id.et_product_manufacturer).text.toString()

            // Валідація
            if (name.isBlank() || description.isBlank() || priceText.isBlank() || category.isBlank()) {
                Toast.makeText(requireContext(), "Заповніть обов'язкові поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val price = priceText.toDouble()
                val quantity = if (quantityText.isBlank()) 1 else quantityText.toInt()
                val year = if (yearText.isBlank()) null else yearText.toInt()

                // Створення об'єкта товару
                val newProduct = Product(
                    name = name,
                    description = description,
                    price = price,
                    category = category,
                    condition = condition,
                    quantity = quantity,
                    imageUrl = imageUrl,
                    inStock = quantity > 0,
                    releaseYear = year,
                    manufacturer = if (manufacturer.isBlank()) null else manufacturer
                )

                // Збереження товару
                viewModel.insertProduct(newProduct)
                dialog.dismiss()
                Toast.makeText(requireContext(), "Товар успішно додано", Toast.LENGTH_SHORT).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Неправильний формат числа", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun setupCategorySpinner(dialogView: View) {
        val categories = arrayOf(
            "Консолі",
            "Ігри NES",
            "Ігри SNES",
            "Ігри Sega Genesis",
            "Ігри PlayStation",
            "Ігри Nintendo 64",
            "Аксесуари",
            "Колекційні предмети"
        )

        val categorySpinner = dialogView.findViewById<AutoCompleteTextView>(R.id.spinner_category)
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        categorySpinner.setAdapter(categoryAdapter)
    }

    private fun setupConditionSpinner(dialogView: View) {
        val conditions = arrayOf(
            "Нове",
            "Як нове",
            "Дуже добре",
            "Добре",
            "Задовільно",
            "Для колекціонерів"
        )

        val conditionSpinner = dialogView.findViewById<AutoCompleteTextView>(R.id.spinner_condition)
        val conditionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, conditions)
        conditionSpinner.setAdapter(conditionAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}