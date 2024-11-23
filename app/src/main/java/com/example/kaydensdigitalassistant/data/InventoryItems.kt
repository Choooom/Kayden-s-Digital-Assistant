package com.example.kaydensdigitalassistant.data

data class InventoryItems(
    val name:String,
    val type: String,
    val price:Double,
    var stock:Double,
    val discount:Double =  1.875
)