package com.assignment3.models

data class OrderItem(
    val orderId: String = "",
    val items: List<CartItem> = emptyList(),
    val status: String = "",
    val createAt: String
)
