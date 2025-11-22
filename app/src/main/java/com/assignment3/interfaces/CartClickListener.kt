package com.assignment3.interfaces

interface CartClickListener {
    fun onIncreaseClick(cartItemId: String)
    fun onDecreaseClick(cartItemId: String)
    fun onDeleteClick(cartItemId: String)
}