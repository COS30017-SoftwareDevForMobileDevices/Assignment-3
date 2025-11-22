package com.assignment3.adapters.products

import androidx.recyclerview.widget.RecyclerView
import com.assignment3.R
import com.assignment3.databinding.ProductCardBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product
import com.bumptech.glide.Glide

class ShopViewHolder(
    private val binding: ProductCardBinding,
    private val clickListener: ProductClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bindProduct(product: Product) = with(binding) {
        if (product.isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_fill_red)
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_outline)
        }

        Glide.with(binding.imgProduct.context)
            .load(product.imageUrl)
            .into(binding.imgProduct)

        txtProductName.text = product.name
        txtProductPrice.text = "$${product.price}"

        btnFavorite.setOnClickListener {
            clickListener.onFavoriteClick(product)
        }

        cardProduct.setOnClickListener {
            clickListener.onProductClick(product)
        }
    }

}