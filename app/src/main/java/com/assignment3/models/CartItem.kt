package com.assignment3.models

import com.google.firebase.firestore.PropertyName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    @get:PropertyName("cart_id")
    @set:PropertyName("cart_id")
    var cartId: String,

    val product: Product,
    val quantity: Int,
    val size: String
) : Parcelable