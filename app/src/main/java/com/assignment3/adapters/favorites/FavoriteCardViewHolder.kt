package com.assignment3.adapters.favorites

import androidx.recyclerview.widget.RecyclerView
import com.assignment3.R
import com.assignment3.databinding.ProductFavoriteBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product
import com.bumptech.glide.Glide

class FavoriteCardViewHolder(
    private val binding: ProductFavoriteBinding,
    private val clickListener: ProductClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bindFavorite(product: Product) = with(binding) {
        Glide.with(imgProduct.context)
            .load(product.imageUrl)
            .into(imgProduct)

        txtProductName.text = product.name
        txtProductPrice.text = product.price.toString()

        // Set the whole card click to go to details
        favoriteProduct.setOnClickListener {
            clickListener.onProductClick(product)
        }

        if (product.isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_fill_red)
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_outline)
        }

        // Set click listener for toggling favorite
        btnFavorite.setOnClickListener {
            clickListener.onFavoriteClick(product)
        }
    }
}