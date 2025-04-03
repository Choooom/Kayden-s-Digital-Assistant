package com.example.kaydensdigitalassistant.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kaydensdigitalassistant.SyncableEntity

@Entity(tableName = "employee_details")
data class EmployeeDetail(
    @PrimaryKey(autoGenerate = true)
    val employeeId: Long = 0,
    val name: String,
    val contactNumber: String,
    val emailAddress: String,
    val birthdate: String,
    val password: String,
    val username: String,
    val userProfile: Bitmap? = null,

    override val lastModified: Long = System.currentTimeMillis(),
    override val isDeleted: Boolean = false,
    override val deviceId: String = ""
) : SyncableEntity
