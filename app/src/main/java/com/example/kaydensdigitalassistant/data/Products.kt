package com.example.kaydensdigitalassistant.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "products")
data class Products(
    @PrimaryKey(autoGenerate = true)
    val productId: Long = 0,
    val productName: String,
    val type: String,
    val normalPrice: Double,
    val discountedPrice: Double,
    val stock: Double,
    @TypeConverters(Converters::class)
    val productIcon: Bitmap? = null
)
