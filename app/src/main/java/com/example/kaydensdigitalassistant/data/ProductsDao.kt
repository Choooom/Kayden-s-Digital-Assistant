package com.example.kaydensdigitalassistant.data

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {
    @Upsert
    suspend fun insertProduct(product: Products): Long

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Products>>

    @Query("UPDATE products SET stock = stock - :quantity WHERE productName = :productName")
    suspend fun updateStockAfterSale(productName: String, quantity: Double)

    @Query("SELECT * FROM products WHERE type = :type")
    fun getProductsByType(type: String): Flow<List<Products>>

    @Query("SELECT productIcon FROM products WHERE productName = :name")
    suspend fun getProductsIconByName(name: String): Bitmap?

    @Update
    suspend fun updateProduct(product: Products)

    @Delete
    suspend fun deleteProduct(product: Products)

    @Query("SELECT DISTINCT type FROM products ORDER BY type ASC")
    fun getAllProductTypes(): Flow<List<String>>
}