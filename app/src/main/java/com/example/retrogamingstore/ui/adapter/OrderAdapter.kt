package com.example.retrogamingstore.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.retrogamingstore.R
import com.example.retrogamingstore.model.Order
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(private val onOrderClick: (Order) -> Unit) :
    ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view, onOrderClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(itemView: View, private val onOrderClick: (Order) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val textOrderId: TextView = itemView.findViewById(R.id.text_order_id)
        private val textOrderDate: TextView = itemView.findViewById(R.id.text_order_date)
        private val textOrderStatus: TextView = itemView.findViewById(R.id.text_order_status)
        private val textOrderTotal: TextView = itemView.findViewById(R.id.text_order_total)
        private val textDeliveryAddress: TextView = itemView.findViewById(R.id.text_delivery_address)

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        fun bind(order: Order) {
            textOrderId.text = "Замовлення #${order.id}"
            textOrderDate.text = dateFormat.format(order.orderDate)

            val statusText = when(order.status) {
                "PENDING" -> "Очікує підтвердження"
                "CONFIRMED" -> "Підтверджено"
                "SHIPPED" -> "Відправлено"
                "DELIVERED" -> "Доставлено"
                "CANCELLED" -> "Скасовано"
                else -> order.status
            }
            textOrderStatus.text = "Статус: $statusText"

            textOrderTotal.text = "Сума: ${order.totalAmount} грн"
            textDeliveryAddress.text = "Адреса доставки: ${order.deliveryAddress}"

            itemView.setOnClickListener {
                onOrderClick(order)
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