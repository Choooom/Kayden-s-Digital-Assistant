package com.example.kaydensdigitalassistant.data

data class CustomerPreference(
    val customerDetails: CustomerDetail,
    val orderPreference: List<ReceiptItem>
)