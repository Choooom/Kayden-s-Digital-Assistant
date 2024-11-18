package com.example.kaydensdigitalassistant.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Products): Long

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Products>>

    @Query("UPDATE products SET stock = stock - :quantity WHERE productId = :productId")
    suspend fun updateStockAfterSale(productId: String, quantity: Double)

    @Query("SELECT * FROM products WHERE type = :type")
    fun getProductsByType(type: String): Flow<List<Products>>
}