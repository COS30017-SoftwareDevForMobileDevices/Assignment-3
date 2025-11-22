package com.assignment3.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName


data class OrderItem(
    var orderId: String,

    @get:PropertyName("order_items")
    @set:PropertyName("order_items")
    var orderItems: List<CartItem>,
    val status: String,

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Timestamp? = null,
)