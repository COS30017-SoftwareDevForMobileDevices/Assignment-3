package com.assignment3.adapters.order

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment3.R
import com.assignment3.databinding.OrderRowBinding
import com.assignment3.models.OrderItem

class OrderViewHolder(
    private val binding: OrderRowBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(order: OrderItem) = with(binding) {
        txtOrderId.text = "Order #${order.orderId}"
        btnStatus.text = order.status
        txtDate.text = order.createAt

        val totalPrice = order.items.sumOf {
            it.product.price * it.quantity
        }

        txtTotalAmount.text = "$$totalPrice"

        if (order.status == "pending") {
            btnStatus.setBackgroundColor(ContextCompat.getColor(root.context, R.color.bg_blue_status))
            btnStatus.setTextColor(ContextCompat.getColor(root.context, R.color.txt_blue_status))
        } else {
            btnStatus.setBackgroundColor(ContextCompat.getColor(root.context, R.color.bg_green_status))
            btnStatus.setTextColor(ContextCompat.getColor(root.context, R.color.txt_green_status))
        }

        recyclerViewOrderItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = OrderItemsAdapter(order.items)
        }
    }
}