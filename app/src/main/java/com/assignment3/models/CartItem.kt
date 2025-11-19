package com.assignment3.models

data class CartItem(
    val cartId: String,
    val product: Product,
    val quantity: Int,
    val size: Double
)