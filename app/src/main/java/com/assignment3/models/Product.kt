package com.assignment3.models

var productList = mutableListOf<Product>()

val PRODUCT_ID_EXTRA = "productExtra"

class Product(
    var imageUrl: Int,
    var name: String,
    var price: Double,
    var productId: Int = -1
)