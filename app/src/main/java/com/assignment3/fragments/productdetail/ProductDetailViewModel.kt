package com.assignment3.fragments.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.Product
import com.assignment3.repositories.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(

    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun fetchProductById(productId: String) {
        viewModelScope.launch {
            _product.value = repository.fetchProductById(productId)
        }
    }
}