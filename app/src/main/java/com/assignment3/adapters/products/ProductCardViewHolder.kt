package com.assignment3.adapters.products

import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.ProductCardBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product

class ProductCardViewHolder(
    private val binding: ProductCardBinding,
    private val clickListener: ProductClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bindProduct(product: Product) = with(binding) {
        txtProductName.text = product.name
        txtProductPrice.text = product.price.toString()

        cardProduct.setOnClickListener {
            clickListener.onClick(product)
        }
    }
}