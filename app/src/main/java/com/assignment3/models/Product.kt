package com.assignment3.models

import com.google.firebase.firestore.PropertyName

var productList = mutableListOf<Product>()

const val PRODUCT_ID_EXTRA = "productExtra"
const val PRODUCT_FAVORITE_CHECK = "isFavorite"

data class Product(
    var productId: String = "",
    val name: String = "",
    val type: String = "",
    val brand: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val gender: String = "",
    val category: String = "",
    var quantity: Int = 0,
    val createdAt: String = "",

    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String = "",

    var size: Double = 0.0,
    var isFavorite: Boolean = false
)