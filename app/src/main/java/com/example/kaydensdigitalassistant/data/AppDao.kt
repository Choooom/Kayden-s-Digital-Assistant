package com.example.kaydensdigitalassistant.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AppDao {
    @Query("DELETE FROM customer_details")
    suspend fun clearCustomerDetails()

    @Query("DELETE FROM sales_items")
    suspend fun clearSalesTable()

    @Query("DELETE FROM products")
    suspend fun clearProductsTable()
}