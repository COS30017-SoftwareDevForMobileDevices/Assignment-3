package com.assignment3.fragments.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.repositories.FavoriteRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repository: FavoriteRepository = FavoriteRepository()
) : ViewModel() {
    private val _addFavoriteResult = MutableLiveData<Result<Boolean>>()
    val addFavoriteResult: LiveData<Result<Boolean>> get() = _addFavoriteResult

    fun addProductToFavorite(userId: String, productId: String) = viewModelScope.launch {
        _addFavoriteResult.value = repository.addProductToFavorite(userId, productId)
    }

}