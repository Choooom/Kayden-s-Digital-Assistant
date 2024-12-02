package com.example.kaydensdigitalassistant.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.kaydensdigitalassistant.GenerateReceipt
import com.example.kaydensdigitalassistant.LocalCustomerViewModel
import com.example.kaydensdigitalassistant.LocalProductsViewModel
import com.example.kaydensdigitalassistant.LocalReceiptViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ReceiptViewModel(private val productsViewModel: ProductsViewModel)  : ViewModel() {


    var receiptItemsState = mutableStateListOf<ReceiptItem>()
    private val _productList = MutableStateFlow<List<Products>>(emptyList())
    val productList = _productList.asStateFlow()

    var isDiscounted by mutableStateOf(false)

    var customerPreference = mutableStateListOf<CustomerPreference>()

    var showProductList by mutableStateOf(false)
    var currentIndex by mutableStateOf(0)

    var toClear by mutableStateOf(false)

    companion object {
        var isCustomerSelected by mutableStateOf(false)

        fun customerPickedCallback(resetSelection: Boolean = false) {
            if (resetSelection) {
                isCustomerSelected = false
            } else {
                isCustomerSelected = true
            }
        }
    }

    init {
        viewModelScope.launch {
            productsViewModel.productsState.collect { products ->
                _productList.value = products
            }
        }
    }

    fun initializeReceiptItem(item: List<ReceiptItem>) {
        receiptItemsState.clear()
        receiptItemsState.addAll(item)
    }

    fun removeReceiptItem(index: Int) {
        receiptItemsState.removeAt(index)
    }

    fun getTotalAmount(): Double {
        return receiptItemsState.sumOf { it.amount * it.quantity }
    }

    fun addProductItem(item: ReceiptItem) {
        receiptItemsState.add(item)
    }

    fun getReceiptList(): List<ReceiptItem> {
        return receiptItemsState
    }

    fun subtractQuantity(quantity: Double, index: Int) {
        if (index in receiptItemsState.indices) {
            if (receiptItemsState[index].quantity >= quantity) {
                receiptItemsState[index] = receiptItemsState[index].copy(quantity = quantity)
            } else {
                receiptItemsState[index] = receiptItemsState[index].copy(quantity = 0.0)
            }
        }
    }

    fun addItem(product: String, type: String) {
        val productItem = _productList.value.find { it.productName == product && it.type == type }

        if (productItem != null) {
            if (currentIndex in receiptItemsState.indices) {
                // Update existing item at currentIndex
                receiptItemsState[currentIndex] = ReceiptItem(
                    name = productItem.productName,
                    type = productItem.type,
                    amount = if(isDiscounted) productItem.discountedPrice else productItem.normalPrice,
                    quantity = 0.0
                )
            } else {
                // Add new item to the list
                receiptItemsState.add(
                    ReceiptItem(
                        name = productItem.productName,
                        type = productItem.type,
                        amount = if(isDiscounted) productItem.discountedPrice else productItem.normalPrice,
                        quantity = 0.0
                    )
                )
                currentIndex = receiptItemsState.lastIndex
            }
        }
    }



    fun addQuantity(quantity: Double, index: Int) {
        if (index in receiptItemsState.indices) {
            receiptItemsState[index] = receiptItemsState[index].copy(quantity = quantity)
        }
        println(getReceiptList())
    }

    fun removeProductItem(index: Int) {
        receiptItemsState.removeAt(index)
    }


    fun preferenceAvailable(customer: CustomerDetail?): Boolean{
        val existingCustomer = customerPreference.find {
            it.customerDetails.name == customer?.name ?: ""
        }

        if(existingCustomer != null) return true
        else return false
    }

    fun updateCustomerPreference(currentCustomer: CustomerDetail) {
        val existingCustomer = customerPreference.find {
            it.customerDetails.name == currentCustomer.name
        }

        if (existingCustomer != null) {
            // Create a new list (deep copy) for orderPreference
            val updatedPreference = existingCustomer.copy(orderPreference = receiptItemsState.toList())
            val index = customerPreference.indexOf(existingCustomer)
            if (index != -1) {
                customerPreference[index] = updatedPreference
            }
            println("Customer name: ${customerPreference[0].customerDetails.name}")
            println("Customer order detail: ${customerPreference[0].orderPreference}")
        } else {
            val newPreference = CustomerPreference(
                customerDetails = currentCustomer,
                // Create a new list (deep copy) for orderPreference
                orderPreference = receiptItemsState.toList()
            )
            customerPreference.add(newPreference)
            println("Customer name: ${customerPreference[0].customerDetails.name}")
            println("Customer order detail: ${customerPreference[0].orderPreference}")
        }
    }
}