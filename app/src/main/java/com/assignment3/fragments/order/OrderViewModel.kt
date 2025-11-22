package com.assignment3.fragments.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.OrderItem
import com.assignment3.repositories.OrderRepository
import kotlinx.coroutines.launch

enum class OrderFilter {
    ALL, PENDING, PROCESSING
}

class OrderViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _allOrders = MutableLiveData<List<OrderItem>>(emptyList())
    val allOrders: LiveData<List<OrderItem>> get() = _allOrders

    private val _filteredOrders = MutableLiveData<List<OrderItem>>(emptyList())
    val filteredOrders: LiveData<List<OrderItem>> get() = _filteredOrders

    fun loadOrders(userId: String) {
        viewModelScope.launch {
            val orders = repository.getAllOrderProducts(userId)
            _allOrders.value = orders
            applyFilter(OrderFilter.ALL)
        }
    }

    fun applyFilter(filter: OrderFilter) {
        val list = _allOrders.value.orEmpty()

        _filteredOrders.value = when (filter) {
            OrderFilter.ALL -> list
            OrderFilter.PENDING -> list.filter { it.status.equals("pending", ignoreCase = true) }
            OrderFilter.PROCESSING -> list.filter { it.status.equals("processing", ignoreCase = true) }
        }
    }
}
