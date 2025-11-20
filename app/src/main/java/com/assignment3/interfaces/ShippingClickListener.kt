package com.assignment3.interfaces

import com.assignment3.models.ShippingAddress

interface ShippingClickListener {
    fun onDeleteClick(shippingId: String)
    fun onUpdateClick(address: ShippingAddress)
    fun onDefaultClick(shippingId: String)
}