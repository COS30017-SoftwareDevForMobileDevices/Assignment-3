package com.assignment3.fragments.profile.shipping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.ShippingAddress
import com.assignment3.repositories.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShippingViewModel(
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _shippingUIState = MutableStateFlow(ShippingUIState())
    val shippingUIState: StateFlow<ShippingUIState> = _shippingUIState
    private val _setDefaultState = MutableLiveData<Boolean>()
    val setDefaultState: LiveData<Boolean> get() = _setDefaultState


    // Get all shipping addresses for the user
    fun getShippingAddresses(userId: String) {
        _shippingUIState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val addresses = repository.getShippingAddresses(userId)

                val defaultAddress = addresses.firstOrNull { it.isDefault }

                _shippingUIState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        addresses = addresses,
                        defaultAddress = defaultAddress
                    )
                }
            } catch (e: Exception) {
                _shippingUIState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load addresses"
                    )
                }
            }
        }
    }


    // Add address
    fun addShippingAddress(userId: String, address: ShippingAddress) {
        _shippingUIState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = repository.addShippingAddress(userId, address)

            if (result) {
                _shippingUIState.update {
                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }
                getShippingAddresses(userId)
            } else {
                _shippingUIState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to add address"
                    )
                }
            }
        }
    }


    // Update address
    fun updateShippingAddress(userId: String, address: ShippingAddress) {
        _shippingUIState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = repository.updateShippingAddress(userId, address)

            if (result) {
                _shippingUIState.update {
                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }
                getShippingAddresses(userId)
            } else {
                _shippingUIState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to update address"
                    )
                }
            }
        }
    }


    // Delete address
    fun deleteShippingAddress(userId: String, shippingId: String) {
        _shippingUIState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = repository.deleteShippingAddress(userId, shippingId)

            if (result) {
                _shippingUIState.update {
                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }
                getShippingAddresses(userId)
            } else {
                _shippingUIState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to delete address"
                    )
                }
            }
        }
    }


    // Default shipping info
    fun setDefaultAddress(userId: String, shippingId: String) {
        viewModelScope.launch {
            val success = repository.setDefaultShippingAddress(userId, shippingId)
            _setDefaultState.value = success
            getShippingAddresses(userId)
        }
    }


    // Reset state after a UI event
    fun resetShippingState() {
        _shippingUIState.update { it.copy(error = null) }
    }
}

data class ShippingUIState(
    val addresses: List<ShippingAddress> = emptyList(),
    val defaultAddress: ShippingAddress? = ShippingAddress(),
    val isLoading: Boolean = false,
    val error: String? = null
)
