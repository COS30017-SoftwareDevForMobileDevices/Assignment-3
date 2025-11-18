package com.assignment3.adapters.favorites

import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.ProductCardBinding
import com.assignment3.databinding.ProductFavoriteBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product
import com.bumptech.glide.Glide

class FavoriteCardViewHolder(
    private val binding: ProductFavoriteBinding,
    private val clickListener: ProductClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bindProduct(product: Product) = with(binding) {
        Glide.with(binding.imgProduct.context)
            .load(product.imageUrl)
            .into(binding.imgProduct)

        txtProductName.text = product.name
        binding.txtProductPrice.text = product.price.toString()

        favoriteProduct.setOnClickListener {
            clickListener.onProductClick(product)
        }
    }
}