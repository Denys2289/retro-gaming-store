package com.example.retrogamingstore.ui.main

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.retrogamingstore.R
import com.example.retrogamingstore.databinding.FragmentListBinding
import com.example.retrogamingstore.model.Product
import com.example.retrogamingstore.ui.adapter.ProductAdapter
import com.example.retrogamingstore.ui.viewmodel.CartViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.File

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewModel: ProductViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var searchView: SearchView
    private lateinit var filterBottomSheet: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        // Налаштування тулбару
        binding.toolbar.inflateMenu(R.menu.menu_list)
        setupSearchView()
        setupFilterButton()

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

    private fun setupSearchView() {
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun setupFilterButton() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_filter -> {
                    showFilterDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun showFilterDialog() {
        filterBottomSheet = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)
        filterBottomSheet.setContentView(view)

        // Встановлення поведінки BottomSheet - розширений вигляд за замовчуванням
        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Налаштування фільтра за сортуванням
        val sortChipGroup = view.findViewById<ChipGroup>(R.id.sort_chip_group)

        // Встановлення початкового вибору на основі поточного значення
        when (viewModel.getCurrentSortOrder()) {
            SortOrder.PRICE_ASC -> view.findViewById<Chip>(R.id.chip_price_low).isChecked = true
            SortOrder.PRICE_DESC -> view.findViewById<Chip>(R.id.chip_price_high).isChecked = true
            SortOrder.NEWEST -> view.findViewById<Chip>(R.id.chip_newest).isChecked = true
            SortOrder.OLDEST -> view.findViewById<Chip>(R.id.chip_oldest).isChecked = true
            else -> view.findViewById<Chip>(R.id.chip_none).isChecked = true
        }

        sortChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    R.id.chip_price_low -> viewModel.setSortOrder(SortOrder.PRICE_ASC)
                    R.id.chip_price_high -> viewModel.setSortOrder(SortOrder.PRICE_DESC)
                    R.id.chip_newest -> viewModel.setSortOrder(SortOrder.NEWEST)
                    R.id.chip_oldest -> viewModel.setSortOrder(SortOrder.OLDEST)
                    R.id.chip_none -> viewModel.setSortOrder(null)
                }
            }
        }

        // Налаштування кнопок у нижній частині
        val applyButton = view.findViewById<Button>(R.id.btn_apply_filter)
        applyButton.setOnClickListener {
            viewModel.applyFilters()
            filterBottomSheet.dismiss()
        }

        val resetButton = view.findViewById<Button>(R.id.btn_reset_filter)
        resetButton.setOnClickListener {
            // Скидання всіх фільтрів
            view.findViewById<Chip>(R.id.chip_none).isChecked = true
            viewModel.resetFilters()
        }

        filterBottomSheet.show()
    }

    private fun observeData() {
        // Спостереження за відфільтрованими товарами
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
            updateEmptyStateVisibility(products.isEmpty())
        }
    }

    private fun updateEmptyStateVisibility(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewProducts.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerViewProducts.visibility = View.VISIBLE
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
            // Передаємо ID продукту замість об'єкта Product
            cartViewModel.addToCart(product.id)
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