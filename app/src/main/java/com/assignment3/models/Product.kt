package com.assignment3.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

const val PRODUCT_ID_EXTRA = "productExtra"
const val PRODUCT_FAVORITE_CHECK = "isFavorite"

@Parcelize
data class Product(
    var productId: String = "",
    var ownerId: String = "",
    val name: String = "",
    val brand: String = "",
    val description: String = "",
    val price: Double = 0.0,  
    var createdAt: String = "",

    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String = "",

    var isFavorite: Boolean = false
) : Parcelable