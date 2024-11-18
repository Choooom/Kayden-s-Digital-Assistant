package com.example.kaydensdigitalassistant.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerDetail): Long

    @Query("SELECT * FROM customer_details")
    fun getAllCustomers(): Flow<List<CustomerDetail>>

    @Query("SELECT * FROM customer_details WHERE customerId = :id")
    suspend fun getCustomerById(id: Long): CustomerDetail?

    // New methods for sorting and searching
    @Query("SELECT * FROM customer_details ORDER BY name ASC")
    fun getCustomersSortedByName(): Flow<List<CustomerDetail>>

    @Query("SELECT * FROM customer_details ORDER BY address ASC")
    fun getCustomersSortedByAddress(): Flow<List<CustomerDetail>>

    @Query("SELECT * FROM customer_details WHERE name LIKE '%' || :searchQuery || '%' OR address LIKE '%' || :searchQuery || '%'")
    fun searchCustomers(searchQuery: String): Flow<List<CustomerDetail>>
}
