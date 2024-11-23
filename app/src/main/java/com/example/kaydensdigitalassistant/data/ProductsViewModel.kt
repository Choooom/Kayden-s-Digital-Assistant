package com.example.kaydensdigitalassistant.data

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class ProductsViewModel(private val repository: ProductsRepository) : ViewModel() {
    val allProducts: LiveData<List<Products>> = repository.allProducts.asLiveData()

    private val _productIcons = mutableStateMapOf<String, Bitmap?>()
    val productIcons: Map<String, Bitmap?> = _productIcons

    fun getProductsByType(type: String): LiveData<List<Products>> {
        return repository.getProductsByType(type).asLiveData()
    }

    fun insertProduct(product: Products) = viewModelScope.launch {
        repository.insertProduct(product)
    }

    fun updateStockAfterSale(productName: String, quantity: Double) = viewModelScope.launch {
        repository.updateStockAfterSale(productName, quantity)
    }

    fun fetchProductIconByName(productName: String) {
        viewModelScope.launch {
            if (!_productIcons.containsKey(productName)) {
                val icon = repository.getProductsIconByName(productName)
                _productIcons[productName] = icon
            }
        }
    }

    class ProductsViewModelFactory(private val repository: ProductsRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProductsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class ProductsRepository(private val productsDao: ProductsDao) {
    val allProducts: Flow<List<Products>> = productsDao.getAllProducts()

    suspend fun insertProduct(product: Products): Long {
        return productsDao.insertProduct(product)
    }

    fun getProductsByType(type: String): Flow<List<Products>> {
        return productsDao.getProductsByType(type)
    }

    suspend fun updateStockAfterSale(productId: String, quantity: Double) {
        productsDao.updateStockAfterSale(productId, quantity)
    }

    suspend fun getProductsIconByName(productName: String): Bitmap? {
        return productsDao.getProductsIconByName(productName)
    }
}
