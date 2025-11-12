package com.assignment3.adapters.products

import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.ProductCardBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product

class ProductCardViewHolder(
    private val productCardBinding: ProductCardBinding,
    private val clickListener: ProductClickListener
) : RecyclerView.ViewHolder(productCardBinding.root) {

    fun bindProduct(product: Product) {
        productCardBinding.imgProduct.setImageResource(product.imageUrl)
        productCardBinding.txtProductName.text = product.name
        productCardBinding.txtProductPrice.text = product.price.toString()

        productCardBinding.cardProduct.setOnClickListener {
            clickListener.onClick(product)
        }
    }
}