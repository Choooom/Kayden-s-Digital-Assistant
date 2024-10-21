package com.example.kaydensdigitalassistant.data

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class SalesViewModel: ViewModel() {
    val salesList = mutableStateListOf<OrderDetails>()

    fun addSales(orderDetails: OrderDetails) {
        salesList.add(orderDetails)

        for(i in salesList){
            println("Contents of Sales List: ${i.customerName} | ${i.customerArea}| ${i.blockLot} | ${i.orderBreakdown} | ${i.totalAmount}")
        }
    }
}