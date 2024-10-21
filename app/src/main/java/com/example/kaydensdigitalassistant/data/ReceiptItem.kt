package com.example.kaydensdigitalassistant.data

data class ReceiptItem(
    val name:String,
    val type: String,
    val amount:Double,
    var quantity:Double,
    val price: Double = amount * quantity
)
