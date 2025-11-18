package com.assignment3.adapters.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.assignment3.databinding.ProductFavoriteBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product

class FavoriteCardAdapter(
    private val clickListener: ProductClickListener
) : ListAdapter<Product, FavoriteCardViewHolder>(FavoriteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductFavoriteBinding.inflate(inflater, parent, false)
        return FavoriteCardViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: FavoriteCardViewHolder, position: Int) {
        holder.bindFavorite(getItem(position))
    }
}


// DiffUtil callback – tells RecyclerView how to compare items
class FavoriteDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem.productId == newItem.productId

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem == newItem
}