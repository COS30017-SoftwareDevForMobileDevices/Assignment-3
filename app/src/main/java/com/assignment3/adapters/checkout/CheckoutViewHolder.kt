package com.assignment3.adapters.checkout

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.ProductCheckoutBinding
import com.assignment3.models.CartItem
import com.bumptech.glide.Glide
import kotlin.math.round

class CheckoutViewHolder(
    private val binding: ProductCheckoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bindProductCheckout(cartItem: CartItem) = with(binding) {
        Glide.with(binding.imgProduct.context)
            .load(cartItem.product.imageUrl)
            .into(binding.imgProduct)

        val priceOnQuantity = cartItem.product.price.toString().toDouble() * cartItem.quantity

        txtProductName.text = cartItem.product.name
        txtProductPrice.text = "$${round(priceOnQuantity)}"
        txtProductSize.text = "Size: ${cartItem.size}"
        txtQuantity.text = "x${cartItem.quantity}"
    }
}