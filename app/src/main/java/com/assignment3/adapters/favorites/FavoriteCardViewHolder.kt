package com.assignment3.adapters.favorites

import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.ProductCardBinding
import com.assignment3.databinding.ProductFavoriteBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product

class FavoriteCardViewHolder(
    private val productFavoriteBinding: ProductFavoriteBinding,
    private val clickListener: ProductClickListener
) : RecyclerView.ViewHolder(productFavoriteBinding.root) {

    fun bindProduct(product: Product) {
        productFavoriteBinding.imgProduct.setImageResource(product.imageUrl)
        productFavoriteBinding.txtProductName.text = product.name
        productFavoriteBinding.txtProductPrice.text = product.price.toString()

        productFavoriteBinding.favoriteProduct.setOnClickListener {
            clickListener.onClick(product)
        }
    }
}