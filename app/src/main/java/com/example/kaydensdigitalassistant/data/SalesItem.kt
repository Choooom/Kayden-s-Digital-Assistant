package com.example.kaydensdigitalassistant.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "sales_items")
data class SalesItem(
    @PrimaryKey(autoGenerate = true)
    val salesId: Long = 0L,
    val customerId: Long,
    val employeeId: Long,
    @TypeConverters(Converters::class)
    val orderDetails: List<ReceiptItem>,
    val totalAmount: Double,
    val dateDelivered: String,
    val timeDelivered: String,
    val paymentMethod: String//newly added
)