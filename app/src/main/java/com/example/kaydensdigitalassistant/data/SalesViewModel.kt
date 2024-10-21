package com.example.kaydensdigitalassistant.data

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.kaydensdigitalassistant.LocalSalesViewModel
import com.example.kaydensdigitalassistant.SalesItem
import kotlin.random.Random

class SalesViewModel: ViewModel() {
    val salesList = mutableStateListOf<OrderDetails>()

    init {
        salesList.addAll(listOf(OrderDetails(
            "Marvin", "Carissa 3-B", "09296726163",
            listOf(ReceiptItem("Red Horse 1000 ML (Mucho)", "Beer", 640.0, 14.00),
            ReceiptItem("Red Horse 500 ML", "Beer", 628.0, 10.0)
            ),
            "Regular", 200.00, "Oct/10/2024 - 10:00 AM", Random.nextInt(10_000_000, 100_000_000)
        ),
            OrderDetails(
                "Ate Neng", "Carissa 3-B", "09296726163",
                listOf(ReceiptItem("Red Horse 1000 ML (Mucho)", "Beer", 640.0, 2.00),
                    ReceiptItem("Red Horse 500 ML", "Beer", 628.0, 2.0)
                ),
                "Regular", 200.00, "Oct/10/2024 - 10:00 AM", Random.nextInt(10_000_000, 100_000_000)
            ),
            OrderDetails(
                "Ate Glo", "Carissa 3-B", "09296726163",
                listOf(ReceiptItem("Red Horse 1000 ML (Mucho)", "Beer", 640.0, 3.00),
                    ReceiptItem("Red Horse 500 ML", "Beer", 628.0, 1.0)
                ),
                "Regular", 200.00, "Oct/10/2024 - 10:00 AM", Random.nextInt(10_000_000, 100_000_000)
            )))
    }

    fun addSales(orderDetails: OrderDetails) {
        salesList.add(orderDetails)
    }
}