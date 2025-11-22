package com.assignment3.interfaces

import com.assignment3.models.Product

interface ShopClickListener {
    fun onProductClick(product: Product)
    fun onFavoriteClick(product: Product)
}