package com.assignment3.adapters.product

import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.UserProductCardBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product
import com.bumptech.glide.Glide

class ProductViewHolder(
    private val binding: UserProductCardBinding,
    private val clickListener: ProductClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bindProductManage(product: Product) = with(binding) {
        Glide.with(imgProduct.context)
            .load(product.imageUrl)
            .into(imgProduct)

        txtProductTitle.text = product.name
        txtProductDescription.text = product.description
        txtPrice.text = "$${product.price}"
        txtBrand.text = product.brand

        btnEdit.setOnClickListener {
            clickListener.onUpdateClick(product)
        }

        btnDelete.setOnClickListener {
            clickListener.onDeleteClick(product.productId)
        }
    }
}