package com.example.kaydensdigitalassistant.data

data class OrderDetails (
    val customerName: String,
    val customerArea: String,
    val contactNumber: String,
    val orderBreakdown: List<ReceiptItem>,
    val pricing: String,
    val totalAmount: Double,
    val timeDelivered: String,
    val orderNumber: Int
){

}