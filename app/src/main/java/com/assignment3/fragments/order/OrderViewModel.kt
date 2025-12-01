package com.assignment3.fragments.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.OrderItem
import com.assignment3.repositories.OrderRepository
import kotlinx.coroutines.launch

enum class OrderFilter {
    ALL, PENDING, PROCESSING, WAITING
}

class OrderViewModel : ViewModel() {

    private val repository = OrderRepository()

    // Buyer orders (All, Pending, Processing tabs)
    private val _buyerOrders = MutableLiveData<List<OrderItem>>(emptyList())

    // Seller orders (Waiting tab - orders to process)
    private val _sellerOrders = MutableLiveData<List<OrderItem>>(emptyList())
    val sellerOrders: LiveData<List<OrderItem>> get() = _sellerOrders

    // Filtered orders for display
    private val _filteredOrders = MutableLiveData<List<OrderItem>>(emptyList())
    val filteredOrders: LiveData<List<OrderItem>> get() = _filteredOrders

    // Current filter state
    private val _currentFilter = MutableLiveData(OrderFilter.ALL)
    val currentFilter: LiveData<OrderFilter> get() = _currentFilter

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Status update result
    private val _updateResult = MutableLiveData<Result<Boolean>?>()
    val updateResult: LiveData<Result<Boolean>?> get() = _updateResult

    // Tab position
    var tabPosition: Int? = 0;


    // Load orders where user is the buyer
    fun loadBuyerOrders(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val orders = repository.getAllOrderProducts(userId)
            _buyerOrders.value = orders
            applyFilter(_currentFilter.value ?: OrderFilter.ALL)
            _isLoading.value = false
        }
    }

    // Load orders where user is the seller
    fun loadSellerOrders(sellerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val orders = repository.getSellerOrders(sellerId)
            _sellerOrders.value = orders

            // Update filtered list if currently on Waiting tab
            if (_currentFilter.value == OrderFilter.WAITING) {
                _filteredOrders.value = orders
            }
            _isLoading.value = false
        }
    }

    // Apply filter based on selected tab
    fun applyFilter(filter: OrderFilter) {
        _currentFilter.value = filter

        _filteredOrders.value = when (filter) {
            OrderFilter.ALL -> _buyerOrders.value.orEmpty()
            OrderFilter.PENDING -> _buyerOrders.value.orEmpty()
                .filter { it.status.equals("pending", ignoreCase = true) }
            OrderFilter.PROCESSING -> _buyerOrders.value.orEmpty()
                .filter { it.status.equals("processing", ignoreCase = true) }
            OrderFilter.WAITING -> _sellerOrders.value.orEmpty()
        }
    }

    // Update single order to processing
    fun markOrderAsProcessing(orderId: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateOrderStatus(orderId, "processing")
            _updateResult.value = result

            if (result.isSuccess) {
                loadSellerOrders(userId)
            }
            _isLoading.value = false
        }
    }

    // Update all pending seller orders to processing
    fun markAllOrdersAsProcessing(userId: String) {
        viewModelScope.launch {
            val pendingOrderIds = _sellerOrders.value
                ?.filter { it.status.equals("pending", ignoreCase = true) }
                ?.map { it.orderId }
                ?: emptyList()

            if (pendingOrderIds.isEmpty()) {
                _updateResult.value = Result.success(false)
                return@launch
            }

            _isLoading.value = true
            val result = repository.updateAllOrdersStatus(pendingOrderIds, "processing")
            _updateResult.value = result

            if (result.isSuccess) {
                loadSellerOrders(userId)
            }
            _isLoading.value = false
        }
    }

    // Clear update result after handling
    fun clearUpdateResult() {
        _updateResult.value = null
    }


    fun clearAllData() {
        _filteredOrders.value = emptyList()
        _buyerOrders.value = emptyList()
        _sellerOrders.value = emptyList()
    }
}