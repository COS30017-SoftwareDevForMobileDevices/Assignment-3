package com.assignment3.fragments.shop

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

    init {
        loadMoreProducts()
    }

    fun loadMoreProducts() {
        if (isLoadingMore || isLastPage) return
        isLoadingMore = true
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            val (newProducts, lastDoc) = repository.fetchProducts(lastVisible)

            if (newProducts.isEmpty()) {
                isLastPage = true
            } else {
                lastVisible = lastDoc
                _uiState.update { state ->
                    state.copy(
                        products = state.products + newProducts,
                        isLoading = false
                    )
                }
            }
            isLoadingMore = false
        }
    }
}

data class ShopUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
