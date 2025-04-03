package com.example.kaydensdigitalassistant.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.kaydensdigitalassistant.SyncableEntity

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
    val productIcon: Bitmap? = null,
    val isHalfable: Boolean = false,

    override val lastModified: Long = System.currentTimeMillis(),
    override val isDeleted: Boolean = false,
    override val deviceId: String = ""
) : SyncableEntity

