package com.example.kaydensdigitalassistant.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.kaydensdigitalassistant.SyncableEntity

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
    val paymentMethod: String,
    val paymentOption: String,
    val deposit: Double = 0.0,
    val referenceNumber: String,

    override val lastModified: Long = System.currentTimeMillis(),
    override val isDeleted: Boolean = false,
    override val deviceId: String = ""
) : SyncableEntity