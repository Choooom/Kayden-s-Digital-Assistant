package com.example.kaydensdigitalassistant.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRoleViewModel : ViewModel() {
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin = _isAdmin.asStateFlow()

    fun setAdminStatus(isAdmin: Boolean) {
        _isAdmin.value = isAdmin
    }
}

class UserRoleViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserRoleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserRoleViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
