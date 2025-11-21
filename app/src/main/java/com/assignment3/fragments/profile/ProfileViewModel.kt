package com.assignment3.fragments.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.models.Product
import com.assignment3.models.User
import com.assignment3.repositories.ProductRepository
import com.assignment3.repositories.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading


    fun loadUserProfile() {
        val uid = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _loading.value = true
            _user.value = repository.getUserInfoById(uid)
            _loading.value = false
        }
    }


    fun updateWallet(amount: Long) {
        val uid = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _loading.value = true

            val success = repository.updateWalletBalance(uid, amount)

            if (success) {
                // Refresh user profile so UI updates immediately
                _user.value = repository.getUserInfoById(uid)
            }

            _loading.value = false
        }
    }
}
