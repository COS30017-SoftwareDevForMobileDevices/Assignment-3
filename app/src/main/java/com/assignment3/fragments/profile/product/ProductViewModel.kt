package com.assignment3.fragments.profile.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.Product
import com.assignment3.repositories.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _productUIState = MutableStateFlow(ProductUIState())
    val productUIState: StateFlow<ProductUIState> = _productUIState

    fun loadUserProducts(userId: String) {
        _productUIState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val products = repository.fetchUserProducts(userId)
                _productUIState.update {
                    it.copy(
                        products = products,
                        isLoading = false,
                        error = null
                    )
                }
                Log.d("Product VM", "User id: $userId")
                Log.d("Product VM", "Products: $products")
            } catch (e: Exception) {
                _productUIState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load products"
                    )
                }
            }
        }
    }

    fun addProduct(userId: String, product: Product) {
        _productUIState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = repository.addProduct(userId, product)
            if (result) {
                _productUIState.update { it.copy(isLoading = false, error = null) }
                loadUserProducts(userId)
            } else {
                _productUIState.update {
                    it.copy(isLoading = false, error = "Failed to add product")
                }
            }
        }
    }

    fun updateProduct(userId: String, product: Product) {
        _productUIState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = repository.updateProduct(product)
            if (result) {
                _productUIState.update { it.copy(isLoading = false, error = null) }
                loadUserProducts(userId)
            } else {
                _productUIState.update {
                    it.copy(isLoading = false, error = "Failed to update product")
                }
            }
        }
    }

    fun deleteProduct(userId: String, productId: String) {
        _productUIState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = repository.deleteProduct(productId)
            if (result) {
                _productUIState.update { it.copy(isLoading = false, error = null) }
                loadUserProducts(userId)
            } else {
                _productUIState.update {
                    it.copy(isLoading = false, error = "Failed to delete product")
                }
            }
        }
    }

    fun resetError() {
        _productUIState.update { it.copy(error = null) }
    }
}

data class ProductUIState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)