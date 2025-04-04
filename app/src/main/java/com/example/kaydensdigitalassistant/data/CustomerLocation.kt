package com.example.kaydensdigitalassistant.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "customer_locations",
    foreignKeys = [
        ForeignKey(
            entity = CustomerDetail::class,
            parentColumns = ["customerId"],
            childColumns = ["customerId"]
        )
    ]
)
data class CustomerLocation(
    @PrimaryKey(autoGenerate = true)
    val locationId: Long = 0L,
    val customerId: Long,
    val latitude: Double,
    val longitude: Double,
)