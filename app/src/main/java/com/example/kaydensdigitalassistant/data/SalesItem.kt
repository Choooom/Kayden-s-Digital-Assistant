package com.example.kaydensdigitalassistant.data

data class SalesItem(
    val customerName:String,
    val customerAddress: String,
    val customerContactNumber: String,
    val orderDetails: List<ReceiptItem>,
    val totalAmount: Double
)