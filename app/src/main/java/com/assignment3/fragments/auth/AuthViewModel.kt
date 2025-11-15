package com.assignment3.fragments.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
    val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<Boolean>>()
    val loginResult: LiveData<Result<Boolean>> get() = _loginResult

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginResult.value = repository.login(email, password)
    }

    private val _registerResult = MutableLiveData<Result<Boolean>>()
    val registerResult: LiveData<Result<Boolean>> get() = _registerResult

    fun register(fullName: String, email: String, password: String) = viewModelScope.launch {
        _registerResult.value = repository.register(fullName, email, password)
    }

    fun isLoggedIn(): Boolean {
        return firebaseUser != null
    }
}
