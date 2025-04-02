package com.example.retrogamingstore.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.retrogamingstore.databinding.ItemOrderProductBinding
import com.example.retrogamingstore.model.OrderItemWithProduct
import java.io.File

class OrderItemAdapter : ListAdapter<OrderItemWithProduct, OrderItemAdapter.OrderItemViewHolder>(OrderItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val orderItem = getItem(position)
        holder.bind(orderItem)
    }

    class OrderItemViewHolder(private val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderItemWithProduct: OrderItemWithProduct) {
            val orderItem = orderItemWithProduct.orderItem
            val product = orderItemWithProduct.product

            binding.tvProductName.text = product.name
            binding.tvProductQuantity.text = "К-сть: ${orderItem.quantity}"
            binding.tvProductPrice.text = "Ціна: ${String.format("%.2f", orderItem.priceAtOrder)} грн"

            // Завантаження зображення товару
            Glide.with(itemView.context)
                .load(File(product.imagePath))
                .into(binding.ivProductImage)
        }
    }

    class OrderItemDiffCallback : DiffUtil.ItemCallback<OrderItemWithProduct>() {
        override fun areItemsTheSame(oldItem: OrderItemWithProduct, newItem: OrderItemWithProduct): Boolean {
            return oldItem.orderItem.id == newItem.orderItem.id
        }

        override fun areContentsTheSame(oldItem: OrderItemWithProduct, newItem: OrderItemWithProduct): Boolean {
            return oldItem == newItem
        }
    }
}