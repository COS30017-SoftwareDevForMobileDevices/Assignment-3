package com.assignment3.models

import com.google.firebase.firestore.PropertyName

data class User(
    var userId: String = "",
    var email: String = "",

    @get:PropertyName("full_name")
    @set:PropertyName("full_name")
    var fullName: String = "",

    @get:PropertyName("shipping_addresses")
    @set:PropertyName("shipping_addresses")
    var shippingAddresses: List<ShippingAddress> = emptyList()
)
