package com.assignment3.fragments.shop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.Product
import com.assignment3.repositories.ProductRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShopViewModel : ViewModel() {

    private val repository = ProductRepository()
    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState

    private var lastVisible: DocumentSnapshot? = null
    private var isLoadingMore = false
    private var isLastPage = false
    private val fullProductList = mutableListOf<Product>()

    init {
        Log.d("ShopViewModel", "ViewModel INIT → loadMoreProducts()")
        loadMoreProducts()
    }

    fun loadMoreProducts() {
        if (isLoadingMore || isLastPage) {
            Log.d("ShopViewModel", "Blocked: loading=$isLoadingMore, lastPage=$isLastPage")
            return
        }

        Log.d("ShopViewModel", "START loading more...")
        isLoadingMore = true
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val (newProducts, lastDoc) = repository.fetchProducts(lastVisible)
                Log.d("ShopViewModel", "Received ${newProducts.size} products")

                if (newProducts.isEmpty()) {
                    isLastPage = true
                    Log.d("ShopViewModel", "END OF LIST")
                } else {
                    fullProductList.addAll(newProducts)
                    lastVisible = lastDoc
                    _uiState.update {
                        it.copy(
                            products = fullProductList.toList(),
                            isLoading = false
                        )
                    }
                    Log.d("ShopViewModel", "Updated UI: ${fullProductList.size} total")
                }
            } catch (e: Exception) {
                Log.e("ShopViewModel", "Error: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            } finally {
                isLoadingMore = false
            }
        }
    }
}

data class ShopUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
