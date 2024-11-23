package com.example.kaydensdigitalassistant.data

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kaydensdigitalassistant.LocalSalesViewModel
import com.example.kaydensdigitalassistant.getCurrentTimeDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.random.Random

class SalesItemViewModel(private val repository: SalesItemRepository) : ViewModel() {
    val receiptItemState = mutableStateListOf<ReceiptItem>()
    val allSalesItems: LiveData<List<SalesItem>> = repository.allSalesItems.asLiveData()

    private val _customerDetail = MutableLiveData<CustomerDetail>()
    val customerDetail: LiveData<CustomerDetail> = _customerDetail

    private val _selectedSalesItem = MutableLiveData<SalesItem>()
    val selectedSalesItem: LiveData<SalesItem> = _selectedSalesItem

    private val _selectedSalesItems = MutableLiveData<List<SalesItem>>()
    val selectedSalesItems: LiveData<List<SalesItem>> = _selectedSalesItems

    fun insertSalesItem(salesItem: SalesItem) = viewModelScope.launch {
        repository.insertSalesItem(salesItem)
    }

    fun getSalesItemsByCustomer(customerId: Long): LiveData<List<SalesItem>> {
        return repository.getSalesItemsByCustomer(customerId).asLiveData()
    }

    fun filterSalesByDate(date: String?) = viewModelScope.launch {
        if (date == null) {
            // Load all sales when "All Sales" is selected
            repository.allSalesItems.collect { salesItems ->
                _selectedSalesItems.value = salesItems.sortedByDescending { it.dateDelivered }
            }
        } else {
            // Filter by the selected date
            repository.getSalesByDate(date).collect { salesItems ->
                _selectedSalesItems.value = salesItems.sortedByDescending { it.dateDelivered }
            }
        }
    }

    fun fetchSalesById(salesId: Long) = viewModelScope.launch {
        repository.getSalesItemsByCustomer(salesId).collect { salesItems ->
            _selectedSalesItem.value = salesItems.firstOrNull()
        }
    }

    fun fetchSalesBySalesId(salesId: Long) = viewModelScope.launch {
        repository.getSalesItemsBySales(salesId).collect { salesItems ->
            _selectedSalesItem.value = salesItems.firstOrNull()
        }
    }

    class SalesItemViewModelFactory(private val repository: SalesItemRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SalesItemViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SalesItemViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class SalesItemRepository(private val salesItemDao: SalesItemDao) {
    val allSalesItems: Flow<List<SalesItem>> = salesItemDao.getAllSalesItems()

    suspend fun insertSalesItem(salesItem: SalesItem): Long {
        return salesItemDao.insertSalesItem(salesItem)
    }

    fun getSalesItemsByCustomer(customerId: Long): Flow<List<SalesItem>> {
        return salesItemDao.getSalesItemsByCustomer(customerId)
    }

    fun getSalesItemsBySales(salesId: Long): Flow<List<SalesItem>> {
        return salesItemDao.getSalesItemsBySales(salesId)
    }

    fun getSalesByDate(date: String): Flow<List<SalesItem>> {
        return salesItemDao.getSalesByDate(date)
    }
}
