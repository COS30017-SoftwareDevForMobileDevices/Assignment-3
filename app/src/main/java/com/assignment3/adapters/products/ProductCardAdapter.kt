package com.assignment3.adapters.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.ProductCardBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product

class ProductCardAdapter(
    private val products: List<Product>,
    private val clickListener: ProductClickListener
) : RecyclerView.Adapter<ProductCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ProductCardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ProductCardBinding.inflate(from, parent, false)
        return ProductCardViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: ProductCardViewHolder, position: Int) {
        holder.bindProduct(products[position])
    }

    override fun getItemCount(): Int = products.size

}