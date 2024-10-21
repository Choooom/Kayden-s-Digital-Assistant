package com.example.kaydensdigitalassistant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kaydensdigitalassistant.data.CustomerDetailViewModel
import com.example.kaydensdigitalassistant.data.ReceiptViewModel
import com.example.kaydensdigitalassistant.data.SalesViewModel

val LocalReceiptViewModel = compositionLocalOf<ReceiptViewModel> { error("No ReceiptViewModel provided") }
val LocalCustomerViewModel = compositionLocalOf<CustomerDetailViewModel> { error("No CustomerViewModel provided") }
val LocalSalesViewModel = compositionLocalOf<SalesViewModel> { error("No SalesViewModel provided") }

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
    val viewModel: CustomerDetailViewModel = viewModel()
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
    val viewModel: SalesViewModel = viewModel()
    ProvideSalesViewModel(viewModel) {
        content()
    }
}

@Composable
fun ProvideSalesViewModel(viewModel: SalesViewModel, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSalesViewModel provides viewModel) {
        content()
    }
}
