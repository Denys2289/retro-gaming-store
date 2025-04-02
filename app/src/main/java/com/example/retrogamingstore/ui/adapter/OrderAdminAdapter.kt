
package com.example.retrogamingstore.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.retrogamingstore.databinding.ItemOrderAdminBinding
import com.example.retrogamingstore.model.Order
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdminAdapter(private val onOrderClick: (Order) -> Unit) :
    ListAdapter<Order, OrderAdminAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderAdminBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    inner class OrderViewHolder(private val binding: ItemOrderAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onOrderClick(getItem(position))
                }
            }
        }

        fun bind(order: Order) {
            binding.tvOrderId.text = "Замовлення #${order.id}"

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            binding.tvOrderDate.text = "Дата: ${dateFormat.format(order.orderDate)}"

            binding.tvOrderTotal.text = "Сума: ${String.format("%.2f", order.totalAmount)} грн"

            // Встановлюємо колір чіпа в залежності від статусу
            binding.chipStatus.text = order.status

            when (order.status) {
                "PENDING" -> {
                    binding.chipStatus.text = "В обробці"
                    binding.chipStatus.setChipBackgroundColorResource(android.R.color.holo_blue_light)
                }
                "CONFIRMED" -> {
                    binding.chipStatus.text = "Підтверджено"
                    binding.chipStatus.setChipBackgroundColorResource(android.R.color.holo_green_light)
                }
                "SHIPPED" -> {
                    binding.chipStatus.text = "Відправлено"
                    binding.chipStatus.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                }
                "DELIVERED" -> {
                    binding.chipStatus.text = "Доставлено"
                    binding.chipStatus.setChipBackgroundColorResource(android.R.color.holo_green_dark)
                }
                "CANCELLED" -> {
                    binding.chipStatus.text = "Скасовано"
                    binding.chipStatus.setChipBackgroundColorResource(android.R.color.holo_red_light)
                }
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}