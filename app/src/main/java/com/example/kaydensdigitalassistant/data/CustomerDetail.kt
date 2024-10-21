package com.example.kaydensdigitalassistant.data

data class CustomerDetail(
    val name: String,
    val gender: String,
    val address: String,
    val contactNumber: String,
    val isOldCustomer: Boolean = false
)