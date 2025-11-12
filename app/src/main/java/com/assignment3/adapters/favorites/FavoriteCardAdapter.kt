package com.assignment3.adapters.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.ProductFavoriteBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product

class FavoriteCardAdapter(
    private val products: List<Product>,
    private val clickListener: ProductClickListener
) : RecyclerView.Adapter<FavoriteCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : FavoriteCardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ProductFavoriteBinding.inflate(from, parent, false)
        return FavoriteCardViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: FavoriteCardViewHolder, position: Int) {
        holder.bindProduct(products[position])
    }

    override fun getItemCount(): Int = products.size

}