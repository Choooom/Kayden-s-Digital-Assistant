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
    var currentCustomer = mutableStateOf(CustomerDetail("","", "", "" , ""))

    init{
        customerDetails.addAll(listOf(
            CustomerDetail("Ate Ning","Female","Complex", "N/A", "092296726163", true),
            CustomerDetail("Malou","Female","C2B", "N/A", "093745864571", true),
            CustomerDetail("Ate Glo","Female","C4A", "B3 L1", "092296726163", true),
            CustomerDetail("Boi Ilong","Male","Phase 7", "B7 L12", "092265126163", true),
            CustomerDetail("Jenny Bunso", "Female","C5A", "B15 L2", "096520726163", true),
            CustomerDetail("Dan","Male","Phase 7", "N/A", "097134926163", true),
            CustomerDetail("Outpost","Female","Complex", "N/A", "0922689163", true),
            CustomerDetail("Ate Ning","Female","Complex", "N/A", "09567566163", true)
        ))
    }

    fun setCustomerDetail(customerDetail: CustomerDetail) {
        currentCustomer.value = customerDetail
        println("Current Customer: ${currentCustomer.value}")
    }

    fun getOldCustomerDetail(name: String): List<CustomerDetail> {
        return customerDetails
    }
}