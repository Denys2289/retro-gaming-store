package com.example.retrogamingstore.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.retrogamingstore.databinding.ItemCheckoutBinding
import com.example.retrogamingstore.model.CartProductWithDetails

class CheckoutItemAdapter : ListAdapter<CartProductWithDetails, CheckoutItemAdapter.ViewHolder>(CheckoutDiffCallback()) {

    class ViewHolder(private val binding: ItemCheckoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartProduct: CartProductWithDetails) {
            binding.textProductName.text = cartProduct.product.name
            binding.textQuantity.text = "x${cartProduct.quantity}"
            binding.textPrice.text = "₴${cartProduct.product.price}"
            binding.textItemTotal.text = "₴${cartProduct.product.price * cartProduct.quantity}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCheckoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class CheckoutDiffCallback : DiffUtil.ItemCallback<CartProductWithDetails>() {
        override fun areItemsTheSame(oldItem: CartProductWithDetails, newItem: CartProductWithDetails): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProductWithDetails, newItem: CartProductWithDetails): Boolean {
            return oldItem == newItem
        }
    }
}