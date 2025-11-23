package com.assignment3.fakes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FakeViewModelFactory(
    private val vm: ViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return vm as T
    }
}
