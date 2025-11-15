package com.assignment3.interfaces

import com.assignment3.models.Product

interface ProductClickListener {
    fun onProductClick(product: Product)
    fun onFavoriteClick(product: Product)
}