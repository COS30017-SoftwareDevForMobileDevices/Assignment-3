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
    private val _favorites = MutableLiveData<List<String>>()
    val favorites: LiveData<List<String>> get() = _favorites


    fun loadFavorites(userId: String) = viewModelScope.launch {
        _favorites.value = repository.fetchUserFavorites(userId)
    }


    fun toggleFavorite(userId: String, productId: String) = viewModelScope.launch {
        val newState = repository.toggleFavorite(userId, productId)

        // Update local LiveData immediately
        val currentList = _favorites.value ?: emptyList()
        _favorites.value = if (newState) {
            currentList + productId
        } else {
            currentList - productId
        }
    }


    fun clearFavorites() {
        _favorites.value = emptyList()
    }
}
