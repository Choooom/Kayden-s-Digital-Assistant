package com.example.kaydensdigitalassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "customer_details")
data class CustomerDetail(
    @PrimaryKey(autoGenerate = true)
    val customerId: Long = 0,
    val name: String,
    val address: String,
    val contactNumber: String,
    @TypeConverters(Converters::class)
    val preferredOrder: List<String> = emptyList(),
    val registrationDate: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
)