package com.example.kaydensdigitalassistant.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalesItem(salesItem: SalesItem): Long

    @Query("SELECT * FROM sales_items")
    fun getAllSalesItems(): Flow<List<SalesItem>>

    @Query("SELECT * FROM sales_items WHERE customerId = :customerId")
    fun getSalesItemsByCustomer(customerId: Long): Flow<List<SalesItem>>

    @Query("SELECT * FROM sales_items WHERE dateDelivered = :date")
    fun getSalesByDate(date: String): Flow<List<SalesItem>>
}
