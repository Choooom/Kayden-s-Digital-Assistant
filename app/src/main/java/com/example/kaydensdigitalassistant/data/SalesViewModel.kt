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
import org.apache.commons.math3.stat.descriptive.summary.Product
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

    val allProducts: LiveData<List<Products>> = productRepository.allProducts.asLiveData()
    val allCustomers: LiveData<List<CustomerDetail>> = customerRepository.allCustomers.asLiveData()

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

    // Add these functions to your SalesItemViewModel class

    fun getWeeklySales(): Flow<Pair<Double, Double>> = flow {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }

        // Current week start and end
        val currentWeekStart = calendar.clone() as java.util.Calendar
        currentWeekStart.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val currentWeekEnd = calendar.clone() as java.util.Calendar
        currentWeekEnd.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)

        // Previous week start and end
        val prevWeekStart = currentWeekStart.clone() as java.util.Calendar
        prevWeekStart.add(java.util.Calendar.WEEK_OF_YEAR, -1)
        val prevWeekEnd = currentWeekEnd.clone() as java.util.Calendar
        prevWeekEnd.add(java.util.Calendar.WEEK_OF_YEAR, -1)

        // Get sales for all days in both weeks and sum them
        var currentWeekSales = 0.0
        var prevWeekSales = 0.0

        val allSales = repository.allSalesItems.first()

        // Calculate current week sales
        calendar.timeInMillis = currentWeekStart.timeInMillis
        while (calendar.timeInMillis <= currentWeekEnd.timeInMillis) {
            val dateStr = dateFormat.format(calendar.time)
            val daySales = allSales
                .filter { it.dateDelivered == dateStr }
                .sumOf { it.totalAmount }
            currentWeekSales += daySales
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        // Calculate previous week sales
        calendar.timeInMillis = prevWeekStart.timeInMillis
        while (calendar.timeInMillis <= prevWeekEnd.timeInMillis) {
            val dateStr = dateFormat.format(calendar.time)
            val daySales = allSales
                .filter { it.dateDelivered == dateStr }
                .sumOf { it.totalAmount }
            prevWeekSales += daySales
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        emit(Pair(currentWeekSales, prevWeekSales))
    }

    fun getMonthlySales(): Flow<Pair<Double, Double>> = flow {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }

        // Current month
        val currentMonth = calendar.get(java.util.Calendar.MONTH)
        val currentYear = calendar.get(java.util.Calendar.YEAR)

        // Previous month
        val prevCalendar = calendar.clone() as java.util.Calendar
        prevCalendar.add(java.util.Calendar.MONTH, -1)
        val prevMonth = prevCalendar.get(java.util.Calendar.MONTH)
        val prevYear = prevCalendar.get(java.util.Calendar.YEAR)

        val allSales = repository.allSalesItems.first()

        // Calculate current month sales
        val currentMonthSales = allSales
            .filter {
                try {
                    val date = dateFormat.parse(it.dateDelivered)
                    calendar.time = date
                    calendar.get(java.util.Calendar.MONTH) == currentMonth &&
                            calendar.get(java.util.Calendar.YEAR) == currentYear
                } catch (e: Exception) {
                    false
                }
            }
            .sumOf { it.totalAmount }

        // Calculate previous month sales
        val prevMonthSales = allSales
            .filter {
                try {
                    val date = dateFormat.parse(it.dateDelivered)
                    calendar.time = date
                    calendar.get(java.util.Calendar.MONTH) == prevMonth &&
                            calendar.get(java.util.Calendar.YEAR) == prevYear
                } catch (e: Exception) {
                    false
                }
            }
            .sumOf { it.totalAmount }

        emit(Pair(currentMonthSales, prevMonthSales))
    }

    fun getWeeklyProfit(): Flow<List<Pair<String, Double>>> = flow {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }

        val weekFormat = SimpleDateFormat("'Week' w", Locale.getDefault())
        val profitMargin = 0.2 // 20% profit margin

        // Get last 4 weeks data
        val weeksData = mutableListOf<Pair<String, Double>>()

        // Start from current week
        val currentWeek = calendar.get(java.util.Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(java.util.Calendar.YEAR)

        // Get all sales
        val allSales = repository.allSalesItems.first()

        // Last 4 weeks
        for (weekOffset in 0 downTo -3) {
            val weekCalendar = calendar.clone() as java.util.Calendar
            weekCalendar.add(java.util.Calendar.WEEK_OF_YEAR, weekOffset)

            val weekOfYear = weekCalendar.get(java.util.Calendar.WEEK_OF_YEAR)
            val year = weekCalendar.get(java.util.Calendar.YEAR)

            // Calculate week start and end
            val weekStart = weekCalendar.clone() as java.util.Calendar
            weekStart.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            val weekEnd = weekCalendar.clone() as java.util.Calendar
            weekEnd.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)

            // Format week label
            val weekLabel = "Week ${weekOfYear}"

            // Calculate sales for the week
            var weekSales = 0.0
            val tmpCalendar = weekStart.clone() as java.util.Calendar

            while (tmpCalendar.timeInMillis <= weekEnd.timeInMillis) {
                val dateStr = dateFormat.format(tmpCalendar.time)
                val daySales = allSales
                    .filter { it.dateDelivered == dateStr }
                    .sumOf { it.totalAmount }
                weekSales += daySales
                tmpCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
            }

            // Calculate profit
            val weekProfit = weekSales * profitMargin
            weeksData.add(Pair(weekLabel, weekProfit))
        }

        emit(weeksData.reversed()) // Return in chronological order
    }

    fun getMonthlyProfit(): Flow<List<Pair<String, Double>>> = flow {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
        val profitMargin = 0.2 // 20% profit margin

        // Get last 6 months data
        val monthsData = mutableListOf<Pair<String, Double>>()

        // Get all sales
        val allSales = repository.allSalesItems.first()

        // Last 6 months
        for (monthOffset in 0 downTo -5) {
            val monthCalendar = calendar.clone() as java.util.Calendar
            monthCalendar.add(java.util.Calendar.MONTH, monthOffset)

            val month = monthCalendar.get(java.util.Calendar.MONTH)
            val year = monthCalendar.get(java.util.Calendar.YEAR)

            // Format month label
            val monthLabel = monthFormat.format(monthCalendar.time)

            // Calculate sales for the month
            val monthSales = allSales
                .filter {
                    try {
                        val date = dateFormat.parse(it.dateDelivered)
                        val saleCal = java.util.Calendar.getInstance()
                        saleCal.time = date
                        saleCal.get(java.util.Calendar.MONTH) == month &&
                                saleCal.get(java.util.Calendar.YEAR) == year
                    } catch (e: Exception) {
                        false
                    }
                }
                .sumOf { it.totalAmount }

            // Calculate profit
            val monthProfit = monthSales * profitMargin
            monthsData.add(Pair(monthLabel, monthProfit))
        }

        emit(monthsData.reversed()) // Return in chronological order
    }

    fun getWeeklySalesCount(): Flow<List<Pair<String, Int>>> = flow {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }

        // Get last 8 weeks data
        val weeksData = mutableListOf<Pair<String, Int>>()

        // Get all sales
        val allSales = repository.allSalesItems.first()

        // Last 8 weeks
        for (weekOffset in 0 downTo -7) {
            val weekCalendar = calendar.clone() as java.util.Calendar
            weekCalendar.add(java.util.Calendar.WEEK_OF_YEAR, weekOffset)

            val weekOfYear = weekCalendar.get(java.util.Calendar.WEEK_OF_YEAR)

            // Calculate week start and end
            val weekStart = weekCalendar.clone() as java.util.Calendar
            weekStart.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            val weekEnd = weekCalendar.clone() as java.util.Calendar
            weekEnd.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)

            // Format week label
            val weekLabel = "Week ${weekOfYear}"

            // Count sales for the week
            var weekSalesCount = 0
            val tmpCalendar = weekStart.clone() as java.util.Calendar

            while (tmpCalendar.timeInMillis <= weekEnd.timeInMillis) {
                val dateStr = dateFormat.format(tmpCalendar.time)
                val daySalesCount = allSales.count { it.dateDelivered == dateStr }
                weekSalesCount += daySalesCount
                tmpCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
            }

            weeksData.add(Pair(weekLabel, weekSalesCount))
        }

        emit(weeksData.reversed()) // Return in chronological order
    }

    fun getMonthlySalesCount(): Flow<List<Pair<String, Int>>> = flow {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())

        // Get last 6 months data
        val monthsData = mutableListOf<Pair<String, Int>>()

        // Get all sales
        val allSales = repository.allSalesItems.first()

        // Last 6 months
        for (monthOffset in 0 downTo -5) {
            val monthCalendar = calendar.clone() as java.util.Calendar
            monthCalendar.add(java.util.Calendar.MONTH, monthOffset)

            val month = monthCalendar.get(java.util.Calendar.MONTH)
            val year = monthCalendar.get(java.util.Calendar.YEAR)

            // Format month label
            val monthLabel = monthFormat.format(monthCalendar.time)

            // Count sales for the month
            val monthSalesCount = allSales
                .count {
                    try {
                        val date = dateFormat.parse(it.dateDelivered)
                        val saleCal = java.util.Calendar.getInstance()
                        saleCal.time = date
                        saleCal.get(java.util.Calendar.MONTH) == month &&
                                saleCal.get(java.util.Calendar.YEAR) == year
                    } catch (e: Exception) {
                        false
                    }
                }

            monthsData.add(Pair(monthLabel, monthSalesCount))
        }

        emit(monthsData.reversed()) // Return in chronological order
    }

    fun getWeeklyProductSales(): Flow<List<ProductSaleCount>> = flow {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }

        // Calculate current week start and end
        val weekStart = calendar.clone() as java.util.Calendar
        weekStart.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val weekEnd = calendar.clone() as java.util.Calendar
        weekEnd.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)

        // Get sales for current week
        val weekSalesDates = mutableListOf<String>()
        val tmpCalendar = weekStart.clone() as java.util.Calendar

        while (tmpCalendar.timeInMillis <= weekEnd.timeInMillis) {
            weekSalesDates.add(dateFormat.format(tmpCalendar.time))
            tmpCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        // Get sales items for these dates
        val allSales = repository.allSalesItems.first()
        val weekSales = allSales.filter { it.dateDelivered in weekSalesDates }

        // Get all products
        val products = productRepository.allProducts.first()

        // Calculate product sales for the week
        val productSales = products.map { product ->
            var totalSales = 0.0

            // For each sale, check if product is in order details and calculate sales
            weekSales.forEach { sale ->
                if (sale.orderDetails.toString().contains(product.productName)) {
                    // Parse order details to extract product quantities and amounts
                    // This is simplified and assumes orderDetails can be parsed correctly
                    try {
                        // Extract quantity and amount for this product from orderDetails
                        // This is a simplified approach and might need adjustment based on your actual orderDetails format
                        val productPattern = "\"productName\":\"${product.productName}\".*?\"quantity\":(\\d+).*?\"amount\":(\\d+\\.?\\d*)".toRegex()
                        val matchResult = productPattern.find(sale.orderDetails.toString())

                        if (matchResult != null && matchResult.groupValues.size >= 3) {
                            val quantity = matchResult.groupValues[1].toIntOrNull() ?: 0
                            val amount = matchResult.groupValues[2].toDoubleOrNull() ?: 0.0
                            totalSales += quantity * amount
                        }
                    } catch (e: Exception) {
                        // Handle parsing exceptions
                    }
                }
            }

            ProductSaleCount(product.productName, totalSales)
        }.filter { it.totalSales > 0 } // Only include products with sales

        emit(productSales.sortedByDescending { it.totalSales })
    }

    fun getMonthlyProductSales(): Flow<List<ProductSaleCount>> = flow {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }

        // Calculate current month period
        val currentMonth = calendar.get(java.util.Calendar.MONTH)
        val currentYear = calendar.get(java.util.Calendar.YEAR)

        // Get all sales
        val allSales = repository.allSalesItems.first()

        // Filter sales for current month
        val monthSales = allSales.filter {
            try {
                val date = dateFormat.parse(it.dateDelivered)
                val saleCal = java.util.Calendar.getInstance()
                saleCal.time = date
                saleCal.get(java.util.Calendar.MONTH) == currentMonth &&
                        saleCal.get(java.util.Calendar.YEAR) == currentYear
            } catch (e: Exception) {
                false
            }
        }

        // Get all products
        val products = productRepository.allProducts.first()

        // Calculate product sales for the month
        val productSales = products.map { product ->
            var totalSales = 0.0

            // For each sale, check if product is in order details and calculate sales
            monthSales.forEach { sale ->
                if (sale.orderDetails.toString().contains(product.productName)) {
                    try {
                        // Extract quantity and amount for this product from orderDetails
                        val productPattern = "\"productName\":\"${product.productName}\".*?\"quantity\":(\\d+).*?\"amount\":(\\d+\\.?\\d*)".toRegex()
                        val matchResult = productPattern.find(sale.orderDetails.toString())

                        if (matchResult != null && matchResult.groupValues.size >= 3) {
                            val quantity = matchResult.groupValues[1].toIntOrNull() ?: 0
                            val amount = matchResult.groupValues[2].toDoubleOrNull() ?: 0.0
                            totalSales += quantity * amount
                        }
                    } catch (e: Exception) {
                        // Handle parsing exceptions
                    }
                }
            }

            ProductSaleCount(product.productName, totalSales)
        }.filter { it.totalSales > 0 } // Only include products with sales

        emit(productSales.sortedByDescending { it.totalSales })
    }

    fun filterSalesByDateRange(startDate: String, endDate: String) = viewModelScope.launch {
        repository.getSalesByDateRange(startDate, endDate).collect { salesItems ->
            _selectedSalesItems.value = salesItems.sortedByDescending { it.dateDelivered }
        }
    }

    // Export sales data for a date range to Excel
    fun exportSalesDataRange(startDate: String, endDate: String, context: Context) {
        viewModelScope.launch {
            val sales = repository.getSalesByDateRange(startDate, endDate).first()
            val customers = customerRepository.allCustomers.first().associateBy { it.customerId }
            val products = productRepository.allProducts.first()

            val exporter = ExcelExporter(context)
            val file = exporter.exportSalesRangeToExcel(
                sales = sales,
                customers = customers,
                products = products,
                startDate = startDate,
                endDate = endDate
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

    fun getSalesByDateRange(startDate: String, endDate: String): Flow<List<SalesItem>> {
        return salesItemDao.getSalesByDateRange(startDate, endDate)
    }
}
