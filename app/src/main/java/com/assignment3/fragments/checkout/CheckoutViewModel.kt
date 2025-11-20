package com.assignment3.fragments.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.CartItem
import com.assignment3.repositories.CheckoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val repository: CheckoutRepository = CheckoutRepository()
) : ViewModel() {

    private val _checkoutUIState = MutableStateFlow(CheckoutUIState())
    val checkoutUIState: StateFlow<CheckoutUIState> = _checkoutUIState


    // Move all cart items → order (with order_items subcollection)
    fun checkout(cartItems: List<CartItem>, userId: String) {
        _checkoutUIState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

        viewModelScope.launch {
            val result = repository.cartItemToOrder(cartItems, userId)

            if (result.isSuccess) {
                _checkoutUIState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                }

                Log.d("Checkout VM", "Checkout success for user $userId")
            } else {
                _checkoutUIState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = result.exceptionOrNull()?.message
                    )
                }

                Log.e("Checkout VM", "Checkout error: ${result.exceptionOrNull()?.message}")
            }
        }
    }



    // Reset success state after checkout completes
    fun resetCheckoutState() {
        _checkoutUIState.update { it.copy(isSuccess = false, error = null) }
    }
}

data class CheckoutUIState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
