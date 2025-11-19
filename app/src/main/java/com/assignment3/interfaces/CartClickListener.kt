package com.assignment3.interfaces

import com.assignment3.models.Product

interface CartClickListener {
    fun onIncreaseClick(cartItemId: String)
    fun onDecreaseClick(cartItemId: String)
    fun onDeleteClick(cartItemId: String)
}