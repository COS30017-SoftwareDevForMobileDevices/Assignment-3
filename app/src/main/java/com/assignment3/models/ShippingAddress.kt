package com.assignment3.models

import com.google.firebase.firestore.PropertyName

data class ShippingAddress(
    @get:PropertyName("shipping_id")
    @set:PropertyName("shipping_id")
    var shippingId: String = "",

    var name: String = "",
    var address: String = "",
    var phone: String = "",

    @get:PropertyName("is_default")
    @set:PropertyName("is_default")
    var isDefault: Boolean = false
)