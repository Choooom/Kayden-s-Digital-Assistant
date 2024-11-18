package com.example.kaydensdigitalassistant

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kaydensdigitalassistant.data.AppDatabase
import com.example.kaydensdigitalassistant.data.CustomerDetailViewModel
import com.example.kaydensdigitalassistant.data.CustomerRepository
import com.example.kaydensdigitalassistant.data.EmployeeDetailViewModel
import com.example.kaydensdigitalassistant.data.EmployeeRepository
import com.example.kaydensdigitalassistant.data.ProductsRepository
import com.example.kaydensdigitalassistant.data.ProductsViewModel
import com.example.kaydensdigitalassistant.data.ReceiptViewModel
import com.example.kaydensdigitalassistant.data.SalesItemRepository
import com.example.kaydensdigitalassistant.data.SalesItemViewModel
val LocalReceiptViewModel = compositionLocalOf<ReceiptViewModel> { error("No ReceiptViewModel provided") }
val LocalCustomerViewModel = compositionLocalOf<CustomerDetailViewModel> { error("No CustomerViewModel provided") }
val LocalSalesViewModel = compositionLocalOf<SalesItemViewModel> { error("No SalesViewModel provided") }
val LocalProductsViewModel = compositionLocalOf<ProductsViewModel> { error("No ProductsViewModel provided") }
val LocalEmployeeViewModel = compositionLocalOf<EmployeeDetailViewModel> { error("No ProductsViewModel provided") }

@Composable
fun ReceiptProvider(content: @Composable () -> Unit) {
    val viewModel: ReceiptViewModel = viewModel()
    ProvideReceiptViewModel(viewModel) {
        content()
    }
}

@Composable
fun ProvideReceiptViewModel(viewModel: ReceiptViewModel, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalReceiptViewModel provides viewModel) {
        content()
    }
}

@Composable
fun CustomerDetailProvider(content: @Composable () -> Unit) {
    val dao = AppDatabase.getInstance(LocalContext.current).customerDetailDao()
    val repository = CustomerRepository(dao)
    val viewModel: CustomerDetailViewModel = viewModel(factory = CustomerDetailViewModel.CustomerDetailViewModelFactory(repository))
    ProvideCustomerDetailViewModel(viewModel) {
        content()
    }
}

@Composable
fun ProvideCustomerDetailViewModel(viewModel: CustomerDetailViewModel, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalCustomerViewModel provides viewModel) {
        content()
    }
}

@Composable
fun SalesProvider(content: @Composable () -> Unit) {
    val salesItemDao = AppDatabase.getInstance(LocalContext.current).salesItemDao()
    val repository = SalesItemRepository(salesItemDao)
    val viewModel: SalesItemViewModel = viewModel(factory = SalesItemViewModel.SalesItemViewModelFactory(repository))
    ProvideSalesViewModel(viewModel) {
        content()
    }
}

@Composable
fun ProvideSalesViewModel(viewModel: SalesItemViewModel, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSalesViewModel provides viewModel) {
        content()
    }
}

@Composable
fun ProductsProvider(content: @Composable () -> Unit) {
    val dao = AppDatabase.getInstance(LocalContext.current).productsDao()
    val repository = ProductsRepository(dao)
    val viewModel: ProductsViewModel = viewModel(factory = ProductsViewModel.ProductsViewModelFactory(repository))
    ProvideProductsViewModel(viewModel) {
        content()
    }
}

@Composable
fun ProvideProductsViewModel(viewModel: ProductsViewModel, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalProductsViewModel provides viewModel) {
        content()
    }

    @Composable
    fun ProvideEmployeeDetailViewModel(viewModel: EmployeeDetailViewModel, content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalEmployeeViewModel provides viewModel) {
            content()
        }
    }

    @Composable
    fun EmployeeDetailProvider(content: @Composable () -> Unit) {
        val dao = AppDatabase.getInstance(LocalContext.current).employeeDetailDao()
        val repository = EmployeeRepository(dao)
        val viewModel: EmployeeDetailViewModel = viewModel(
            factory = EmployeeDetailViewModel.EmployeeDetailViewModelFactory(repository)
        )
        ProvideEmployeeDetailViewModel(viewModel) {
            content()
        }
    }
}
