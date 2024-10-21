package com.example.kaydensdigitalassistant.data

data class OrderDetails (
    val customerName: String,
    val customerArea: String,
    val blockLot: String,
    val orderBreakdown: List<ReceiptItem>,
    val pricing: String,
    val totalAmount: Double
){

}