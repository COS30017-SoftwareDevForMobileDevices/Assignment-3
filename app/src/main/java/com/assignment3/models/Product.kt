package com.assignment3.models

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
    val quantity: Int = 0,
    val createdAt: String = "",
    val imageUrl: String = "",
    var isFavorite: Boolean = false
)