package com.example.retrogamingstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.retrogamingstore.R
import com.example.retrogamingstore.model.Product

class ProductAdapter(private val onProductClicked: (Product) -> Unit) :
    ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view, onProductClicked)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    class ProductViewHolder(
        itemView: View,
        private val onProductClicked: (Product) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val productImage: ImageView = itemView.findViewById(R.id.product_image)
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)
        private val productCategory: TextView = itemView.findViewById(R.id.product_category)

        fun bind(product: Product) {
            productName.text = product.name
            productPrice.text = "₴${product.price}"
            productCategory.text = product.category

            // Завантаження зображення за допомогою Glide
            if (product.imageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(product.imageUrl)
                    .placeholder(R.drawable.placeholder_game)
                    .error(R.drawable.placeholder_game)
                    .into(productImage)
            } else {
                productImage.setImageResource(R.drawable.placeholder_game)
            }

            // Встановлення обробника кліків
            itemView.setOnClickListener {
                onProductClicked(product)
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}