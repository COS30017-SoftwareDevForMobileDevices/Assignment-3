package com.assignment3.adapters.orders

import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.ProductOrderBinding
import com.assignment3.models.OrderItem
import com.bumptech.glide.Glide

class OrderCardViewHolder(
    private val binding: ProductOrderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindProductOrder(orderItem: OrderItem) = with(binding) {
        val firstItem = orderItem.orderItems.first()

        Glide.with(binding.imageViewProduct.context)
            .load(firstItem.product.imageUrl)
            .into(binding.imageViewProduct)

        val priceOnQuantity = firstItem.product.price * firstItem.quantity

        txtViewProductName.text = firstItem.product.name
        txtViewPrice.text = "$$priceOnQuantity"
        txtViewSize.text = "Size: ${firstItem.size}"
        txtViewQuantity.text = "x${firstItem.quantity}"
    }
}