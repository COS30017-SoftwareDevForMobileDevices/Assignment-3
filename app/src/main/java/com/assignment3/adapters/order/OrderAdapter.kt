package com.assignment3.adapters.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.assignment3.databinding.OrderRowBinding
import com.assignment3.interfaces.OrderClickListener
import com.assignment3.models.OrderItem

class OrderAdapter(
    private val clickListener: OrderClickListener? = null,
    private val isSellerView: Boolean = false
) : ListAdapter<OrderItem, OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = OrderRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding, clickListener, isSellerView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<OrderItem>() {
    override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean =
        oldItem.orderId == newItem.orderId

    override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean =
        oldItem == newItem
}