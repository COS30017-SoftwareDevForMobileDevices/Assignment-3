package com.assignment3.adapters.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.assignment3.databinding.ProductCardBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product

class ProductCardAdapter(
    private val clickListener: ProductClickListener
) : ListAdapter<Product, ProductCardViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductCardBinding.inflate(inflater, parent, false)
        return ProductCardViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: ProductCardViewHolder, position: Int) {
        holder.bindProduct(getItem(position))
    }
}

// DiffUtil callback – tells RecyclerView how to compare items
class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem.productId == newItem.productId

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem == newItem
}