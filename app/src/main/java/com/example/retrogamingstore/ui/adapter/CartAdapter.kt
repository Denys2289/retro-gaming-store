package com.example.retrogamingstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.retrogamingstore.R
import com.example.retrogamingstore.model.Product
import java.io.File

class CartAdapter(
    private val onIncrement: (Long) -> Unit,
    private val onDecrement: (Long) -> Unit,
    private val onRemove: (Product) -> Unit
) : ListAdapter<Product, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view, onIncrement, onDecrement, onRemove)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    class CartViewHolder(
        itemView: View,
        private val onIncrement: (Long) -> Unit,
        private val onDecrement: (Long) -> Unit,
        private val onRemove: (Product) -> Unit,
    ) : RecyclerView.ViewHolder(itemView) {

        private val productImage: ImageView = itemView.findViewById(R.id.cart_product_image)
        private val productName: TextView = itemView.findViewById(R.id.cart_product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.cart_product_price)
        private val productQuantity: TextView = itemView.findViewById(R.id.cart_product_quantity)
        private val btnIncrement: Button = itemView.findViewById(R.id.btn_increment)
        private val btnDecrement: Button = itemView.findViewById(R.id.btn_decrement)
        private val btnRemove: Button = itemView.findViewById(R.id.btn_remove)

        fun bind(product: Product) {
            productName.text = product.name
            productPrice.text = "₴${product.price}"
            productQuantity.text = "Кількість: ${product.quantity}"

            // Завантаження зображення
            if (product.imagePath.isNotEmpty()) {
                val imageFile = File(product.imagePath)
                if (imageFile.exists()) {
                    Glide.with(itemView.context)
                        .load(imageFile)
                        .placeholder(R.drawable.placeholder_game)
                        .error(R.drawable.placeholder_game)
                        .into(productImage)
                } else {
                    productImage.setImageResource(R.drawable.placeholder_game)
                }
            } else {
                productImage.setImageResource(R.drawable.placeholder_game)
            }

            // Встановлення обробників кліків на кнопки
            btnIncrement.setOnClickListener {
                onIncrement(product.id)
            }

            btnDecrement.setOnClickListener {
                onDecrement(product.id)
            }

            btnRemove.setOnClickListener {
                onRemove(product)
            }
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}