package com.assignment3.interfaces

import com.assignment3.models.Product

interface ProductClickListener {
    fun onDeleteClick(productId: String)
    fun onUpdateClick(product: Product)
}