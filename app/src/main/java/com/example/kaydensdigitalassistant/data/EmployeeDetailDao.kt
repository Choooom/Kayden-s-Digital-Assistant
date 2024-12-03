package com.example.kaydensdigitalassistant.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDetailDao {
    @Upsert
    suspend fun insertEmployee(employee: EmployeeDetail): Long

    @Query("SELECT * FROM employee_details WHERE username = :username AND password = :password")
    suspend fun loginEmployee(username: String, password: String): EmployeeDetail?

    @Query("SELECT * FROM employee_details")
    fun getAllEmployees(): Flow<List<EmployeeDetail>>

    @Update
    suspend fun updateEmployee(employee: EmployeeDetail)

    @Delete
    suspend fun deleteEmployee(employee: EmployeeDetail)

    @Query("DELETE FROM employee_details")
    suspend fun clearAllEmployees()

    @Query("UPDATE employee_details SET password = :newPassword WHERE emailAddress = :email")
    suspend fun updatePassword(email: String, newPassword: String)

}
