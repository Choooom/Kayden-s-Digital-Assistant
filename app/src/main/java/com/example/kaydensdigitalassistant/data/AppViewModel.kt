package com.example.kaydensdigitalassistant.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class AppViewModel(private val repository: AppRepository) : ViewModel() {

    fun clearAllData() = viewModelScope.launch {
        repository.clearAllTables()
    }

    class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class AppRepository(private val dao: AppDao) {
    suspend fun clearAllTables() {
        dao.clearCustomerDetails()
        dao.clearSalesTable()
        dao.clearProductsTable()
    }
}