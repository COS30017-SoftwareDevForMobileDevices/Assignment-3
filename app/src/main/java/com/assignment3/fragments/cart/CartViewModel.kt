package com.assignment3.fragments.cart

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.CartItem
import com.assignment3.repositories.CartRepository
import com.assignment3.repositories.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository = CartRepository(),
    private val favoriteRepository: FavoriteRepository = FavoriteRepository()
) : ViewModel() {
    private val _cartUIState = MutableStateFlow(CartUIState())
    val cartUIState: StateFlow<CartUIState> = _cartUIState

    private val _cartProduct = MutableLiveData<List<String>>()


    fun loadAllCartProducts(userId: String) {
        _cartUIState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val products = repository.fetchAllCartProducts(userId)
            _cartProduct.value = products.map { it.cartId }

            _cartUIState.update { state ->
                state.copy(
                    cartItems = products.map { it.copy() },
                    isLoading = false
                )
            }

            _cartUIState.collect { state ->
                Log.d("Cart VM", state.cartItems.toString())
            }
        }
    }

    fun addCartItem(userId: String, productId: String, size: String) = viewModelScope.launch {
        val product = favoriteRepository.getProduct(productId) ?: return@launch

        val result = repository.addToCart(userId, product, size)

        if (result.isSuccess) {
            // Reload full list to reflect changes (add or increment)
            loadAllCartProducts(userId)
        } else {
            _cartUIState.update { it.copy(error = result.exceptionOrNull()?.message) }
        }
    }


    fun clearCartItems() {
        _cartProduct.value = emptyList()
        _cartUIState.update { it.copy(cartItems = emptyList()) }
    }
}

data class CartUIState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)