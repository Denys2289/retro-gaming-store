package com.example.retrogamingstore.ui.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.retrogamingstore.R
import com.example.retrogamingstore.databinding.FragmentAdminHomeBinding
import com.example.retrogamingstore.model.Product
import com.example.retrogamingstore.ui.adapter.ProductAdapter
import com.example.retrogamingstore.utils.ImageUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class AdminHomeFragment : Fragment() {

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewModel: AdminViewModel

    // Uri вибраного зображення
    private var selectedImageUri: Uri? = null
    private var currentImagePath: String? = null
    private var isEditMode = false
    private var editingProductId: Long = 0

    // ActivityResult для отримання зображення з галереї
    private val getImageFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                selectedImageUri = uri

                // Оновлення попереднього перегляду
                val imagePreview = currentDialog?.findViewById<ImageView>(R.id.iv_product_image_preview)
                val noImageText = currentDialog?.findViewById<TextView>(R.id.tv_no_image_selected)

                imagePreview?.let { preview ->
                    Glide.with(requireContext())
                        .load(uri)
                        .into(preview)

                    // Приховуємо текст про відсутність зображення
                    noImageText?.visibility = View.GONE
                }
            }
        }
    }

    // Поточне діалогове вікно
    private var currentDialog: View? = null
    private var alertDialog: AlertDialog? = null

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
        setupProfileButton()
        setupManageOrdersButton()
        observeProducts()
    }

    private fun setupProfileButton() {
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_adminHomeFragment_to_adminProfileFragment)
        }
    }

    private fun setupManageOrdersButton() {
        binding.btnManageOrders.setOnClickListener {
            findNavController().navigate(R.id.action_adminHomeFragment_to_adminOrdersListFragment)
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            // Обробка кліку на товар - відкриваємо діалог редагування
            showEditProductDialog(product)
        }

        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    private fun setupAddButton() {
        binding.btnAddProduct.setOnClickListener {
            isEditMode = false
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
        currentDialog = dialogView

        // Скидаємо Uri вибраного зображення
        selectedImageUri = null

        // Налаштування випадаючих списків
        setupCategorySpinner(dialogView)
        setupConditionSpinner(dialogView)

        // Налаштування кнопки вибору зображення
        setupImageSelectionButton(dialogView)

        alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        // Налаштування кнопок
        dialogView.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            alertDialog?.dismiss()
        }

        dialogView.findViewById<View>(R.id.btn_add_product).setOnClickListener {
            handleProductAddition(dialogView, alertDialog!!)
        }

        alertDialog?.show()
    }

    private fun showEditProductDialog(product: Product) {
        isEditMode = true
        editingProductId = product.id
        currentImagePath = product.imagePath

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_product, null)
        currentDialog = dialogView

        // Заповнюємо поля даними товару
        dialogView.findViewById<TextInputEditText>(R.id.et_product_name).setText(product.name)
        dialogView.findViewById<TextInputEditText>(R.id.et_product_description).setText(product.description)
        dialogView.findViewById<TextInputEditText>(R.id.et_product_price).setText(product.price.toString())
        dialogView.findViewById<TextInputEditText>(R.id.et_product_quantity).setText(product.quantity.toString())
        product.releaseYear?.let {
            dialogView.findViewById<TextInputEditText>(R.id.et_product_year).setText(it.toString())
        }
        product.manufacturer?.let {
            dialogView.findViewById<TextInputEditText>(R.id.et_product_manufacturer).setText(it)
        }

        // Завантажуємо зображення
        val imagePreview = dialogView.findViewById<ImageView>(R.id.iv_product_image_preview)
        val noImageText = dialogView.findViewById<TextView>(R.id.tv_no_image_selected)

        if (product.imagePath.isNotEmpty()) {
            val imageFile = File(product.imagePath)
            if (imageFile.exists()) {
                Glide.with(requireContext())
                    .load(imageFile)
                    .into(imagePreview)
                noImageText.visibility = View.GONE
            }
        }

        // Налаштування випадаючих списків
        setupCategorySpinner(dialogView, product.category)
        setupConditionSpinner(dialogView, product.condition)

        // Налаштування кнопки вибору зображення
        setupImageSelectionButton(dialogView)

        alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        // Налаштування кнопок
        dialogView.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            alertDialog?.dismiss()
        }

        dialogView.findViewById<View>(R.id.btn_save_product).setOnClickListener {
            handleProductUpdate(dialogView, alertDialog!!, product)
        }

        dialogView.findViewById<View>(R.id.btn_delete_product).setOnClickListener {
            showDeleteConfirmationDialog(product)
        }

        alertDialog?.show()
    }

    private fun showDeleteConfirmationDialog(product: Product) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Видалення товару")
            .setMessage("Ви дійсно хочете видалити товар '${product.name}'?")
            .setPositiveButton("Так") { _, _ ->
                // Виконати "м'яке" видалення
                viewModel.softDeleteProduct(product.id)
                alertDialog?.dismiss()
                Toast.makeText(requireContext(), "Товар видалено", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }

    private fun setupImageSelectionButton(dialogView: View) {
        dialogView.findViewById<View>(R.id.btn_select_image).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getImageFromGallery.launch(intent)
        }
    }

    private fun handleProductAddition(dialogView: View, dialog: AlertDialog) {
        // Отримання даних з полів вводу
        val name = dialogView.findViewById<TextInputEditText>(R.id.et_product_name).text.toString()
        val description = dialogView.findViewById<TextInputEditText>(R.id.et_product_description).text.toString()
        val priceText = dialogView.findViewById<TextInputEditText>(R.id.et_product_price).text.toString()
        val category = dialogView.findViewById<AutoCompleteTextView>(R.id.spinner_category).text.toString()
        val condition = dialogView.findViewById<AutoCompleteTextView>(R.id.spinner_condition).text.toString()
        val quantityText = dialogView.findViewById<TextInputEditText>(R.id.et_product_quantity).text.toString()
        val yearText = dialogView.findViewById<TextInputEditText>(R.id.et_product_year).text.toString()
        val manufacturer = dialogView.findViewById<TextInputEditText>(R.id.et_product_manufacturer).text.toString()

        // Валідація
        if (name.isBlank() || description.isBlank() || priceText.isBlank() || category.isBlank()) {
            Toast.makeText(requireContext(), "Заповніть обов'язкові поля", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Будь ласка, виберіть зображення товару", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val price = priceText.toDouble()
            val quantity = if (quantityText.isBlank()) 1 else quantityText.toInt()
            val year = if (yearText.isBlank()) null else yearText.toInt()

            // Зберігаємо зображення у внутрішню пам'ять
            val imagePath = ImageUtils.saveImageToInternalStorage(requireContext(), selectedImageUri!!)

            if (imagePath != null) {
                // Створення об'єкта товару
                val newProduct = Product(
                    name = name,
                    description = description,
                    price = price,
                    category = category,
                    condition = condition,
                    quantity = quantity,
                    imagePath = imagePath,
                    inStock = quantity > 0,
                    releaseYear = year,
                    manufacturer = if (manufacturer.isBlank()) null else manufacturer
                )

                // Збереження товару
                viewModel.insertProduct(newProduct)
                dialog.dismiss()
                Toast.makeText(requireContext(), "Товар успішно додано", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Помилка при збереженні зображення", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Неправильний формат числа", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleProductUpdate(dialogView: View, dialog: AlertDialog, oldProduct: Product) {
        // Отримання даних з полів вводу
        val name = dialogView.findViewById<TextInputEditText>(R.id.et_product_name).text.toString()
        val description = dialogView.findViewById<TextInputEditText>(R.id.et_product_description).text.toString()
        val priceText = dialogView.findViewById<TextInputEditText>(R.id.et_product_price).text.toString()
        val category = dialogView.findViewById<AutoCompleteTextView>(R.id.spinner_category).text.toString()
        val condition = dialogView.findViewById<AutoCompleteTextView>(R.id.spinner_condition).text.toString()
        val quantityText = dialogView.findViewById<TextInputEditText>(R.id.et_product_quantity).text.toString()
        val yearText = dialogView.findViewById<TextInputEditText>(R.id.et_product_year).text.toString()
        val manufacturer = dialogView.findViewById<TextInputEditText>(R.id.et_product_manufacturer).text.toString()

        // Валідація
        if (name.isBlank() || description.isBlank() || priceText.isBlank() || category.isBlank()) {
            Toast.makeText(requireContext(), "Заповніть обов'язкові поля", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val price = priceText.toDouble()
            val quantity = if (quantityText.isBlank()) 1 else quantityText.toInt()
            val year = if (yearText.isBlank()) null else yearText.toInt()

            // Визначення шляху до зображення
            var imagePath = currentImagePath

            // Якщо вибрано нове зображення, зберігаємо його
            if (selectedImageUri != null) {
                val newImagePath = ImageUtils.saveImageToInternalStorage(requireContext(), selectedImageUri!!)
                if (newImagePath != null) {
                    imagePath = newImagePath
                } else {
                    Toast.makeText(requireContext(), "Помилка при збереженні зображення", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            if (imagePath != null) {
                // Створення оновленого об'єкта товару
                val updatedProduct = oldProduct.copy(
                    name = name,
                    description = description,
                    price = price,
                    category = category,
                    condition = condition,
                    quantity = quantity,
                    imagePath = imagePath,
                    inStock = quantity > 0,
                    releaseYear = year,
                    manufacturer = if (manufacturer.isBlank()) null else manufacturer
                )

                // Оновлення товару
                viewModel.updateProduct(updatedProduct)
                dialog.dismiss()
                Toast.makeText(requireContext(), "Товар успішно оновлено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Потрібно вибрати зображення", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Неправильний формат числа", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCategorySpinner(dialogView: View, selectedCategory: String? = null) {
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

        // Якщо це редагування, встановлюємо вибрану категорію
        selectedCategory?.let {
            categorySpinner.setText(it, false)
        }
    }

    private fun setupConditionSpinner(dialogView: View, selectedCondition: String? = null) {
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

        // Якщо це редагування, встановлюємо вибраний стан
        selectedCondition?.let {
            conditionSpinner.setText(it, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentDialog = null
        alertDialog?.dismiss()
        alertDialog = null
    }
}