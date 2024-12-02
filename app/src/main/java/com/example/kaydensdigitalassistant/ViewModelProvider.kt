package com.example.kaydensdigitalassistant

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kaydensdigitalassistant.data.AppDatabase
import com.example.kaydensdigitalassistant.data.AppRepository
import com.example.kaydensdigitalassistant.data.AppViewModel
import com.example.kaydensdigitalassistant.data.CustomerDetailViewModel
import com.example.kaydensdigitalassistant.data.CustomerLocationRepository
import com.example.kaydensdigitalassistant.data.CustomerRepository
import com.example.kaydensdigitalassistant.data.EmployeeDetailViewModel
import com.example.kaydensdigitalassistant.data.EmployeeRepository
import com.example.kaydensdigitalassistant.data.LocationViewModel
import com.example.kaydensdigitalassistant.data.LocationViewModelFactory
import com.example.kaydensdigitalassistant.data.ProductsRepository
import com.example.kaydensdigitalassistant.data.ProductsViewModel
import com.example.kaydensdigitalassistant.data.ReceiptViewModel
import com.example.kaydensdigitalassistant.data.SalesItemRepository
import com.example.kaydensdigitalassistant.data.SalesItemViewModel
import com.example.kaydensdigitalassistant.data.UserRoleViewModel
import com.example.kaydensdigitalassistant.data.UserRoleViewModelFactory

val LocalReceiptViewModel = compositionLocalOf<ReceiptViewModel> { error("No ReceiptViewModel provided") }
val LocalCustomerViewModel = compositionLocalOf<CustomerDetailViewModel> { error("No CustomerViewModel provided") }
val LocalSalesViewModel = compositionLocalOf<SalesItemViewModel> { error("No SalesViewModel provided") }
val LocalProductsViewModel = compositionLocalOf<ProductsViewModel> { error("No ProductsViewModel provided") }
val LocalEmployeeViewModel = compositionLocalOf<EmployeeDetailViewModel> { error("No ProductsViewModel provided") }
val LocalAppViewModel = compositionLocalOf<AppViewModel> { error("No AppViewModel provided") }
val LocalLocationViewModel = compositionLocalOf<LocationViewModel> { error("No LocationViewModel provided") }
val LocalUserRoleViewModel = compositionLocalOf<UserRoleViewModel> { error("No UserRoleViewModel provided") }

@Composable
fun UserRoleProvider(content: @Composable () -> Unit) {
    val viewModel: UserRoleViewModel = viewModel(
        factory = UserRoleViewModelFactory()
    )
    ProvideUserRoleViewModel(viewModel) {
        content()
    }
}

@Composable
fun ProvideUserRoleViewModel(viewModel: UserRoleViewModel, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalUserRoleViewModel provides viewModel) {
        content()
    }
}

@Composable
fun LocationProvider(content: @Composable () -> Unit) {
    val dao = AppDatabase.getInstance(LocalContext.current).customerLocationDao()
    val repository = CustomerLocationRepository(dao)
    val viewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(repository)
    )
    ProvideLocationViewModel(viewModel) {
        content()
    }
}
@Composable
fun ProvideLocationViewModel(viewModel: LocationViewModel, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLocationViewModel provides viewModel) {
        content()
    }
}

@Composable
fun ReceiptProvider(content: @Composable () -> Unit) {
    val productsViewModel = LocalProductsViewModel.current
    val viewModel: ReceiptViewModel = viewModel { ReceiptViewModel(productsViewModel) }
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

@Composable
fun ProvideAppViewModel(viewModel: AppViewModel, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppViewModel provides viewModel) {
        content()
    }
}

@Composable
fun AppProvider(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val dao = AppDatabase.getInstance(context).appDao()
    val repository = AppRepository(dao)
    val viewModel: AppViewModel = viewModel(factory = AppViewModel.AppViewModelFactory(repository))

    ProvideAppViewModel(viewModel) {
        content()
    }
}

