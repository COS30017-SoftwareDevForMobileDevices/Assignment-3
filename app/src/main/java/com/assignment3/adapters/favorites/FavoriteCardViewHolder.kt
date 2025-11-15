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

    fun bindProduct(product: Product) = with(productFavoriteBinding)  {
        //imgProduct.setImageResource(product.imageUrl)
        txtProductName.text = product.name
        productFavoriteBinding.txtProductPrice.text = product.price.toString()

        favoriteProduct.setOnClickListener {
            clickListener.onProductClick(product)
        }
    }
}