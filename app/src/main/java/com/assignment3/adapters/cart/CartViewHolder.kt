package com.assignment3.adapters.cart

import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.ProductCartBinding
import com.assignment3.interfaces.CartClickListener
import com.assignment3.models.CartItem
import com.bumptech.glide.Glide

class CartViewHolder(
    private val binding: ProductCartBinding,
    private val clickListener: CartClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bindProductCart(cartItem: CartItem) = with(binding) {
        Glide.with(binding.imgProduct.context)
            .load(cartItem.product.imageUrl)
            .into(binding.imgProduct)

        val priceOnQuantity = cartItem.product.price.toString().toDouble() * cartItem.quantity

        txtProductName.text = cartItem.product.name
        txtProductPrice.text = "$${priceOnQuantity}"
        txtProductSize.text = "Size: ${cartItem.size}"
        txtQuantity.text = cartItem.quantity.toString()

        btnIncrease.setOnClickListener {
            clickListener.onIncreaseClick(cartItem.cartId)
        }

        btnDecrease.setOnClickListener {
            clickListener.onDecreaseClick(cartItem.cartId)
        }

        btnDelete.setOnClickListener {
            clickListener.onDeleteClick(cartItem.cartId)
        }
    }
}