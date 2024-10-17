package com.example.kaydensdigitalassistant.data

class ReceiptItem(
    val name:String,
    val amount:Double,
    val quantity:Double,
    val price:Double = amount * quantity
)
