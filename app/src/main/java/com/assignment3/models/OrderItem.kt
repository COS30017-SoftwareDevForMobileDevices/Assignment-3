package com.assignment3.models

data class OrderItem(
    val orderId: String = "",
    val items: List<CartItem> = emptyList(),
    val status: String = "",
    val createAt: String = "",

    // Seller info
    val sellerId: String = "",

    // Buyer info for seller view
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerAddress: String = "",
    val buyerPhone: String = ""
)