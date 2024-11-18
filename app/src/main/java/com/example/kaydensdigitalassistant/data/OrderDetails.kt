package com.example.kaydensdigitalassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "order_details")
@TypeConverters(Converters::class)
data class OrderDetails (
    @PrimaryKey(autoGenerate = true)
    val orderId: Long = 0,
    val customerName: String,
    val customerArea: String,
    val contactNumber: String,
    @TypeConverters(Converters::class)
    val orderBreakdown: List<ReceiptItem>,
    val pricing: String,
    val totalAmount: Int,
    val timeDelivered: String,
){

}