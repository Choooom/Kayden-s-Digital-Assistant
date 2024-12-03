package com.example.kaydensdigitalassistant.data

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kaydensdigitalassistant.ExcelExporter
import com.example.kaydensdigitalassistant.LocalSalesViewModel
import com.example.kaydensdigitalassistant.getCurrentTimeDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

class SalesItemViewModel(private val repository: SalesItemRepository, private val customerRepository: CustomerRepository, private val productRepository: ProductsRepository) : ViewModel() {
    val receiptItemState = mutableStateListOf<ReceiptItem>()
    val allSalesItems: LiveData<List<SalesItem>> = repository.allSalesItems.asLiveData()

    private val _customerDetail = MutableLiveData<CustomerDetail>()
    val customerDetail: LiveData<CustomerDetail> = _customerDetail

    private val _selectedSalesItem = MutableLiveData<SalesItem>()
    val selectedSalesItem: LiveData<SalesItem> = _selectedSalesItem

    private val _selectedSalesItems = MutableLiveData<List<SalesItem>>()
    val selectedSalesItems: LiveData<List<SalesItem>> = _selectedSalesItems

    private val _searchResults = MutableStateFlow<List<SalesItem>>(emptyList())
    val searchResults: StateFlow<List<SalesItem>> = _searchResults.asStateFlow()

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

    fun getTodayVsYesterdaySales(): Flow<Pair<Double, Double>> = flow {
        val today = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }.format(Date())

        val yesterday = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }.format(Date().apply { time -= 24 * 60 * 60 * 1000 })

        combine(
            repository.getSalesByDate(today),
            repository.getSalesByDate(yesterday)
        ) { todaySales, yesterdaySales ->
            Pair(
                todaySales.sumOf { it.totalAmount },
                yesterdaySales.sumOf { it.totalAmount }
            )
        }.collect { emit(it) }
    }

    fun getProfit(): Flow<List<Pair<String, Double>>> = flow {
        val today = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }.format(Date())

        val yesterday = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }.format(Date().apply { time -= 24 * 60 * 60 * 1000 })

        combine(
            repository.getSalesByDate(today),
            repository.getSalesByDate(yesterday)
        ) { todaySales, yesterdaySales ->
            listOf(
                Pair(today, todaySales.sumOf { it.totalAmount * 0.2 }),
                Pair(yesterday, yesterdaySales.sumOf { it.totalAmount * 0.2 })
            )
        }.collect { emit(it) }
    }

    fun getNumberOfSales(): Flow<List<Pair<String, Int>>> = flow {
        repository.allSalesItems.map { sales ->
            sales.groupBy { it.dateDelivered }
                .map { (date, items) ->
                    Pair(date, items.size)
                }
                .sortedByDescending { it.first }
        }.collect { emit(it) }
    }

    fun fetchSalesBySalesId(salesId: Long) = viewModelScope.launch {
        repository.getSalesItemsBySales(salesId).collect { salesItems ->
            _selectedSalesItem.value = salesItems.firstOrNull()
        }
    }

    fun getProductSalesCount(): Flow<List<ProductSaleCount>> {
        return repository.getProductSalesCount()
    }

    fun deleteSalesById(salesId: Long) = viewModelScope.launch {
        repository.deleteSalesById(salesId)
    }

    fun searchSales(query: String) {
        viewModelScope.launch {
            repository.searchSales(query)
                .collect { results ->
                    _searchResults.value = results
                }
        }
    }

    fun exportSalesData(selectedDate: String, context: Context) {
        viewModelScope.launch {
            val sales = repository.getSalesByDate(selectedDate).first()
            val customers = customerRepository.allCustomers.first()
                .associateBy { it.customerId }
            val products = productRepository.allProducts.first()

            val exporter = ExcelExporter(context)
            val file = exporter.exportSalesToExcel(
                sales = sales,
                customers = customers,
                products = products,
                selectedDate = selectedDate
            )

            shareExcelFile(context, file)
        }
    }


    class SalesItemViewModelFactory(
        private val repository: SalesItemRepository,
        private val customerRepository: CustomerRepository,
        private val productRepository: ProductsRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SalesItemViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SalesItemViewModel(repository, customerRepository, productRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}

private fun shareExcelFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share Sales Report"))
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

    fun getProductSalesCount(): Flow<List<ProductSaleCount>> {
        return salesItemDao.getProductSalesCount()
    }

    suspend fun deleteSalesById(salesId: Long) {
        salesItemDao.deleteSalesById(salesId)
    }

    fun searchSales(query: String): Flow<List<SalesItem>> {
        return salesItemDao.searchSales(query)
    }
}
