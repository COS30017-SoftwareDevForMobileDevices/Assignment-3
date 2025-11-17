package com.assignment3.fragments.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment3.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _firebaseUserFlow = MutableStateFlow<FirebaseUser?>(null)
    val firebaseUserFlow: StateFlow<FirebaseUser?> get() = _firebaseUserFlow
    val firebaseUser: FirebaseUser? get() = _firebaseUserFlow.value


    init {
        val auth = FirebaseAuth.getInstance()
        _firebaseUserFlow.value = auth.currentUser

        auth.addAuthStateListener { firebaseAuth ->
            _firebaseUserFlow.value = firebaseAuth.currentUser
        }
    }


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


    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }


    fun isLoggedIn(): Boolean {
        return firebaseUser != null
    }
}