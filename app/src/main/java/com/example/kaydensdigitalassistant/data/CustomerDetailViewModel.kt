package com.example.kaydensdigitalassistant.data

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CustomerDetailViewModel(private val repository: CustomerRepository) : ViewModel() {
    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    var currentCustomer = mutableStateOf(CustomerDetail(name = "", contactNumber = "", address = ""))

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val customerDetails = combine(
        _sortOrder,
        _searchQuery
    ) { sortOrder, query ->
        when {
            query.isNotEmpty() -> repository.searchCustomers(query)
            sortOrder == SortOrder.BY_NAME -> repository.getCustomersSortedByName()
            sortOrder == SortOrder.BY_ADDRESS -> repository.getCustomersSortedByAddress()
            else -> repository.allCustomers
        }
    }.flatMapLatest { it }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insertCustomer(customer: CustomerDetail) = viewModelScope.launch {
        repository.insertCustomer(customer)
        currentCustomer.value = customer
    }

    enum class SortOrder {
        NONE,
        BY_NAME,
        BY_ADDRESS
    }

    class CustomerDetailViewModelFactory(private val repository: CustomerRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CustomerDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CustomerDetailViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class CustomerRepository(private val customerDetailDao: CustomerDetailDao) {
    val allCustomers: Flow<List<CustomerDetail>> = customerDetailDao.getAllCustomers()

    fun getCustomersSortedByName() = customerDetailDao.getCustomersSortedByName()

    fun getCustomersSortedByAddress() = customerDetailDao.getCustomersSortedByAddress()

    fun searchCustomers(query: String) = customerDetailDao.searchCustomers(query)

    suspend fun insertCustomer(customer: CustomerDetail): Long {
        return customerDetailDao.insertCustomer(customer)
    }
}
