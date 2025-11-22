package com.assignment3.fragments.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.OrderItem
import com.assignment3.repositories.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderViewModel(
    private val repository: OrderRepository = OrderRepository()
) : ViewModel() {

    private val _orderUIState = MutableStateFlow(OrderUIState())
    val orderUIState: StateFlow<OrderUIState> = _orderUIState

    private val _orderIds = MutableLiveData<List<String>>()
    val orderIds: LiveData<List<String>> get() = _orderIds


    fun loadAllOrderProducts(userId: String) {
        _orderUIState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val orders: List<OrderItem> = repository.getAllOrderProducts(userId)

            _orderIds.value = orders.map { it.orderId }

            _orderUIState.update { state ->
                state.copy(
                    orderItems = orders.map { it.copy() },
                    isLoading = false,
                    error = null
                )
            }

            _orderUIState.collect { state ->
                Log.d("Order VM", state.orderItems.toString())
            }
        }
    }

    fun clearOrders() {
        _orderIds.value = emptyList()
        _orderUIState.update { it.copy(orderItems = emptyList()) }
    }
}

data class OrderUIState(
    val orderItems: List<OrderItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
