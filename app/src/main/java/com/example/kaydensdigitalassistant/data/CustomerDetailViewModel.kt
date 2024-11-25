package com.example.kaydensdigitalassistant.data

import android.app.Application
import androidx.compose.runtime.State
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
    val allCustomers: LiveData<List<CustomerDetail>> = repository.allCustomers.asLiveData()
    
    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()
    private val dao: CustomerDetailDao = AppDatabase.getInstance(Application()).customerDetailDao()

    var isNewCustomer = mutableStateOf(false)

    private val _currentCustomer = mutableStateOf<CustomerDetail>(CustomerDetail(name = "", address = "", contactNumber = "", customerId = 0))
    val currentCustomer: State<CustomerDetail> = _currentCustomer

    private val _newCurrentCustomer = mutableStateOf<CustomerDetail>(CustomerDetail(name = "", address = "", contactNumber = "", customerId = 0))
    val newCurrentCustomer: State<CustomerDetail> = _newCurrentCustomer

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Set current customer to selected customer
    fun setCurrentCustomer(customer: CustomerDetail) {
        _currentCustomer.value = customer
    }

    // Reset current customer when switching between screens or actions
    fun resetCurrentCustomer() {
        _currentCustomer.value = CustomerDetail(name = "", address = "", contactNumber = "", customerId = 0)
    }

    fun fetchCurrentCustomer(): State<CustomerDetail>{
        return _currentCustomer
    }

    fun fetchCurrentNewCustomer(): State<CustomerDetail>{
        return _newCurrentCustomer
    }

    fun setNewCurrentCustomer(customer: CustomerDetail) {
        _newCurrentCustomer.value = customer
    }

    // Reset current customer when switching between screens or actions
    fun resetNewCurrentCustomer() {
        _newCurrentCustomer.value = CustomerDetail(name = "", address = "", contactNumber = "", customerId = 0)
    }

    fun fetchNewCurrentCustomer(): State<CustomerDetail>{
        return _newCurrentCustomer
    }

    // Combine sorting and search queries to fetch customer details
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

    // Set sorting order
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    // Update search query
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Insert a new customer and set it as the current customer
    fun insertCustomer(customer: CustomerDetail) = viewModelScope.launch {
        // Reset current customer before inserting
        _newCurrentCustomer.value = CustomerDetail(name = "", address = "", contactNumber = "", customerId = 0)

        val generatedId = dao.insertCustomer(customer)
        val customerWithId = customer.copy(customerId = generatedId)

        // Set the new customer
        _newCurrentCustomer.value = customerWithId

        println("DEBUG: Inserted Customer ID: $generatedId")
        println("DEBUG: Current Customer After Insert: ${_newCurrentCustomer.value}")
    }

    // Search for customers based on query and update the current customer
    fun searchAndUpdateCustomer(query: String) = viewModelScope.launch {
        // Collect the Flow from repository.searchCustomers
        repository.searchCustomers(query).collect { customerList ->
            if (customerList.isNotEmpty()) {
                val matchedCustomer = customerList.first()
                val customerWithId = matchedCustomer.copy(customerId = matchedCustomer.customerId)
                _currentCustomer.value = customerWithId // Update current customer with the matched customer
            }
        }
    }

    // Fetch a customer by their ID
    suspend fun fetchCustomerById(customerId: Long): CustomerDetail? {
        return repository.getCustomerByCustomerId(customerId)
    }

    fun deleteCustomer(customer: CustomerDetail) = viewModelScope.launch {
        repository.deleteCustomer(customer)
    }

    fun showCustomerLocation(customer: CustomerDetail) {

    }

    fun updateCustomer(customer: CustomerDetail) = viewModelScope.launch {
        repository.updateCustomer(customer)
    }

    // Enum class to handle sorting order
    enum class SortOrder {
        NONE,
        BY_NAME,
        BY_ADDRESS
    }

    // Factory class for creating the ViewModel with the repository
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

    suspend fun updateCustomer(customer: CustomerDetail) {
        customerDetailDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: CustomerDetail) {
        customerDetailDao.deleteCustomer(customer)
    }

    suspend fun insertCustomer(customer: CustomerDetail): Long {
        return customerDetailDao.insertCustomer(customer)
    }

    suspend fun getCustomerByCustomerId(id: Long): CustomerDetail? {
        return customerDetailDao.getCustomerById(id)
    }

}
