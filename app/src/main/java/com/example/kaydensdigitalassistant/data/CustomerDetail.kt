package com.example.kaydensdigitalassistant.data

data class CustomerDetail(
    val name: String,
    val gender: String,
    val area: String,
    val blockLot: String,
    val contactNumber: String,
    val isOldCustomer: Boolean = false
)