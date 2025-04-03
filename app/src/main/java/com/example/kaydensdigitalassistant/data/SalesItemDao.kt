package com.example.kaydensdigitalassistant.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalesItem(salesItem: SalesItem): Long

    @Query("SELECT * FROM sales_items")
    fun getAllSalesItems(): Flow<List<SalesItem>>

    @Query("SELECT * FROM sales_items WHERE customerId = :customerId")
    fun getSalesItemsByCustomer(customerId: Long): Flow<List<SalesItem>>

    @Query("SELECT * FROM sales_items WHERE salesId = :salesId")
    fun getSalesItemsBySales(salesId: Long): Flow<List<SalesItem>>

    @Query("SELECT * FROM sales_items WHERE dateDelivered = :date")
    fun getSalesByDate(date: String): Flow<List<SalesItem>>

    @Update
    suspend fun updateSales(salesItem: SalesItem): Int

    @Query("""
    SELECT p.productName, SUM(
        CASE 
            WHEN si.orderDetails LIKE '%' || p.productName || '%' 
            THEN (
                CAST(SUBSTR(
                    si.orderDetails, 
                    INSTR(si.orderDetails, '"amount":') + 9,
                    INSTR(SUBSTR(si.orderDetails, INSTR(si.orderDetails, '"amount":') + 9), ',') - 1
                ) AS FLOAT) *
                CAST(SUBSTR(
                    si.orderDetails,
                    INSTR(si.orderDetails, '"quantity":') + 11,
                    INSTR(SUBSTR(si.orderDetails, INSTR(si.orderDetails, '"quantity":') + 11), ',') - 1
                ) AS FLOAT)
            )
            ELSE 0 
        END
    ) as totalSales
    FROM products p
    LEFT JOIN sales_items si ON si.orderDetails LIKE '%' || p.productName || '%'
    GROUP BY p.productName
""")
    fun getProductSalesCount(): Flow<List<ProductSaleCount>>

    // Add these new queries
    @Query("DELETE FROM sales_items WHERE salesId = :salesId")
    suspend fun deleteSalesById(salesId: Long)

    @Query("SELECT * FROM sales_items WHERE salesId = :salesId")
    suspend fun getSalesItemByIdOnce(salesId: Long): SalesItem?

    @Query("SELECT * FROM sales_items WHERE salesId = :id")
    suspend fun getSalesById(id: Long): SalesItem?

    @Query("SELECT * FROM sales_items WHERE lastModified > :timestamp AND isDeleted = 0")
    suspend fun getSalesModifiedSince(timestamp: Long): List<SalesItem>

    @Query("UPDATE sales_items SET isDeleted = 1, lastModified = :timestamp WHERE salesId = :id")
    suspend fun markAsDeleted(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("""
    SELECT si.* FROM sales_items si
    INNER JOIN customer_details cd ON si.customerId = cd.customerId
    WHERE cd.name LIKE '%' || :searchQuery || '%'
    OR cd.address LIKE '%' || :searchQuery || '%'
    OR cd.contactNumber LIKE '%' || :searchQuery || '%'
    OR si.salesId LIKE '%' || :searchQuery || '%'
    OR si.dateDelivered LIKE '%' || :searchQuery || '%'
    OR si.timeDelivered LIKE '%' || :searchQuery || '%'
    OR si.paymentMethod LIKE '%' || :searchQuery || '%'
    OR si.paymentOption LIKE '%' || :searchQuery || '%'
    OR si.referenceNumber LIKE '%' || :searchQuery || '%'
    OR si.orderDetails LIKE '%' || :searchQuery || '%'
""")

    fun searchSales(searchQuery: String): Flow<List<SalesItem>>
}

data class ProductSaleCount(
    val productName: String,
    val totalSales: Double
)
