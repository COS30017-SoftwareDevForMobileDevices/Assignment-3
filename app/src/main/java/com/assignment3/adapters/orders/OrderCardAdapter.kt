package com.assignment3.adapters.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.assignment3.databinding.ProductOrderBinding
import com.assignment3.models.OrderItem

class OrderCardAdapter() : ListAdapter<OrderItem, OrderCardViewHolder>(OrderDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderCardViewHolder {
        val binding = ProductOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderCardViewHolder, position: Int) {
        holder.bindProductOrder(getItem(position))
    }
}

class OrderDiffCallBack : DiffUtil.ItemCallback<OrderItem>() {
    override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean =
        oldItem.orderId == newItem.orderId

    override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean =
        oldItem == newItem
}