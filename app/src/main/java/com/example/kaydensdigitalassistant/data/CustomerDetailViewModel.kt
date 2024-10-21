package com.example.kaydensdigitalassistant.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kaydensdigitalassistant.LocalCustomerViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomerDetailViewModel: ViewModel() {

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

    private val _customerDetailStateFlow = MutableStateFlow<CustomerDetail?>(null)
    val customerDetailStateFlow: StateFlow<CustomerDetail?> = _customerDetailStateFlow.asStateFlow()
    var customerDetails = mutableStateListOf<CustomerDetail>()
    var currentCustomer = mutableStateOf(CustomerDetail("","", "", ""))

    init{
        customerDetails.addAll(listOf(
            CustomerDetail("Ate Ning","Female","Complex",  "092296726163", true),
            CustomerDetail("Malou","Female","C2B", "093745864571", true),
            CustomerDetail("Ate Glo","Female","C4A",  "092296726163", true),
            CustomerDetail("Boi Ilong","Male","Phase 7",  "092265126163", true),
            CustomerDetail("Jenny Bunso", "Female","C5A",  "096520726163", true),
            CustomerDetail("Dan","Male","Phase 7",  "097134926163", true),
            CustomerDetail("Outpost","Female","Complex",  "0922689163", true),
            CustomerDetail("Ate Ning","Female","Complex",  "09567566163", true)
        ))
    }

    fun setCustomerDetail(customerDetail: CustomerDetail) {
        currentCustomer.value = customerDetail
        println("Current Customer: ${currentCustomer.value}")
    }

    fun addCustomerDetail(customerDetail: CustomerDetail) {
        customerDetails.add(customerDetail)
    }

    fun getOldCustomerDetail(name: String): List<CustomerDetail> {
        return customerDetails
    }
}