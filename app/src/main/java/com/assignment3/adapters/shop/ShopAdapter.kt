package com.assignment3.adapters.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.assignment3.databinding.ProductCardBinding
import com.assignment3.interfaces.ShopClickListener
import com.assignment3.models.Product

class ShopAdapter(
    private val clickListener: ShopClickListener
) : ListAdapter<Product, ShopViewHolder>(ShopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductCardBinding.inflate(inflater, parent, false)
        return ShopViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bindProduct(getItem(position))
    }
}

// DiffUtil callback – tells RecyclerView how to compare items
class ShopDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem.productId == newItem.productId

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem == newItem
}