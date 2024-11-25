package com.example.kaydensdigitalassistant.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerLocationDao {
    @Upsert
    suspend fun insertLocation(location: CustomerLocation): Long

    @Query("DELETE FROM customer_locations")
    suspend fun clearAllLocations()

    @Query("SELECT * FROM customer_locations WHERE customerId = :customerId")
    fun getLocationByCustomerId(customerId: Long): Flow<CustomerLocation?>

    @Query("DELETE FROM customer_locations WHERE customerId = :customerId")
    suspend fun deleteLocationByCustomerId(customerId: Long)

    @Query("INSERT INTO customer_locations (customerId, latitude, longitude) VALUES (:customerId, :latitude, :longitude)")
    suspend fun insertTestData(customerId: Long, latitude: Double, longitude: Double)

    @Query("SELECT EXISTS(SELECT customerId FROM customer_details WHERE customerId = :customerId)")
    suspend fun customerExists(customerId: Long): Boolean

    @Query("SELECT * FROM customer_locations WHERE customerId = :customerId")
    suspend fun getLocationByCustomerIdSync(customerId: Long): CustomerLocation?

    @Update
    suspend fun updateLocation(location: CustomerLocation)
}