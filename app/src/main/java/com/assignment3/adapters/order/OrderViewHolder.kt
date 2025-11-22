package com.assignment3.adapters.order

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment3.R
import com.assignment3.databinding.OrderRowBinding
import com.assignment3.interfaces.OrderClickListener
import com.assignment3.models.OrderItem

class OrderViewHolder(
    private val binding: OrderRowBinding,
    private val clickListener: OrderClickListener?,
    private val isSellerView: Boolean
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(order: OrderItem) = with(binding) {
        txtOrderId.text = "Order #${order.orderId.takeLast(6)}"
        btnStatus.text = order.status
        txtDate.text = order.createAt

        // Calculate total price
        val totalPrice = order.items.sumOf { it.product.price * it.quantity }
        txtTotalAmount.text = "$$totalPrice"

        // Set status badge color
        if (order.status == "pending") {
            btnStatus.setBackgroundColor(ContextCompat.getColor(root.context, R.color.bg_blue_status))
            btnStatus.setTextColor(ContextCompat.getColor(root.context, R.color.txt_blue_status))
        } else {
            btnStatus.setBackgroundColor(ContextCompat.getColor(root.context, R.color.bg_green_status))
            btnStatus.setTextColor(ContextCompat.getColor(root.context, R.color.txt_green_status))
        }

        // Show/hide buyer info section based on view type
        if (isSellerView && order.buyerName.isNotEmpty()) {
            buyerInfoContainer.visibility = View.VISIBLE
            txtBuyerName.text = order.buyerName
            txtBuyerAddress.text = order.buyerAddress
            txtBuyerPhone.text = order.buyerPhone

            // Make status clickable for seller view
            btnStatus.isClickable = true
            btnStatus.setOnClickListener {
                clickListener?.onStatusClick(order.orderId)
            }
        } else {
            buyerInfoContainer.visibility = View.GONE
            btnStatus.isClickable = false
            btnStatus.setOnClickListener(null)
        }

        // Setup nested RecyclerView for order items
        recyclerViewOrderItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = OrderItemsAdapter(order.items)
        }
    }
}