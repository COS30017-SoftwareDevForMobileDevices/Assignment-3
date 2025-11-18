package com.assignment3.fragments.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.Product
import com.assignment3.repositories.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repository: FavoriteRepository = FavoriteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState

    private val _favoritesIds = MutableLiveData<List<String>>()
    val favoritesIds: LiveData<List<String>> get() = _favoritesIds


    fun loadFavorites(userId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val products = repository.fetchUserFavorites(userId)
            _favoritesIds.value = repository.fetchUserFavoriteId(userId)

            Log.d("Favorite ViewModel", products.toString())

            _uiState.update { state ->
                state.copy(
                    favorites = products.map { it.copy(isFavorite = true) },
                    isLoading = false
                )
            }
        }
    }


    fun toggleFavorite(userId: String, productId: String) = viewModelScope.launch {
        val added = repository.toggleFavorite(userId, productId)

        // Update product ids list for home
        val currentIds = _favoritesIds.value ?: emptyList()
        _favoritesIds.value = if (added) currentIds + productId else currentIds - productId

        // Update product list for favorites
        _uiState.update { state ->
            val currentProducts = state.favorites
            val newProducts = if (added) {
                val product = repository.getProduct(productId)
                if (product != null) {
                    currentProducts + product.copy(isFavorite = true)
                } else currentProducts
            } else {
                currentProducts.filter { it.productId != productId }
            }
            state.copy(favorites = newProducts)
        }
    }


    fun clearFavorites() {
        _favoritesIds.value = emptyList()
        _uiState.update { it.copy(favorites = emptyList()) }
    }
}

data class FavoriteUiState(
    val favorites: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)