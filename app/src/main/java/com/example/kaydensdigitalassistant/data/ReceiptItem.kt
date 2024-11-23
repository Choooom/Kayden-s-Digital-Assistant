package com.example.kaydensdigitalassistant.data

data class ReceiptItem(
    val name:String,
    val type: String,
    val amount:Double,
    var quantity:Double,
    var isDiscount: Boolean = false,
    var paymentOption: String = "Cash"
)
