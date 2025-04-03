package com.example.kaydensdigitalassistant

import com.example.kaydensdigitalassistant.data.AppDatabase
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.data.CustomerLocation
import com.example.kaydensdigitalassistant.data.EmployeeDetail
import com.example.kaydensdigitalassistant.data.Products
import com.example.kaydensdigitalassistant.data.SalesItem

class ConflictResolver(private val database: AppDatabase) {

    /**
     * Resolves conflicts between local and remote data
     * @param localData The local entity
     * @param remoteData The remote entity received during sync
     * @return The resolved entity that should be saved
     */
    suspend fun <T : SyncableEntity> resolveConflict(localData: T?, remoteData: T): SyncableEntity {
        // If local data doesn't exist, use remote data
        if (localData == null) {
            return remoteData
        }

        // If remote data is marked as deleted
        if (remoteData.isDeleted) {
            // If remote deletion is newer than local modification, use remote (deleted)
            return if (remoteData.lastModified > localData.lastModified) {
                remoteData
            } else {
                // Otherwise keep local version
                localData
            }
        }

        // Neither is deleted, use the most recently modified version
        return if (remoteData.lastModified > localData.lastModified) {
            // For special cases like inventory where we might need to merge values rather than replace
            when (remoteData) {
                is Products -> mergeProductData(localData as Products, remoteData)
                else -> remoteData
            }
        } else {
            localData
        }
    }

    /**
     * Special merge logic for product stock levels
     */
    private suspend fun mergeProductData(localProduct: Products, remoteProduct: Products): Products {
        // For products, we might want special handling for stock levels
        // This avoids potential data loss when both devices update stock independently

        // If devices are different and both modified stock, add the difference
        if (localProduct.deviceId != remoteProduct.deviceId) {
            val stockDifference = remoteProduct.stock - getOriginalStock(remoteProduct.productId, remoteProduct.deviceId)

            // Create a new product with merged data (most recent + stock calculation)
            return remoteProduct.copy(
                stock = localProduct.stock + stockDifference
            )
        }

        return remoteProduct
    }

    /**
     * Get the original stock level before modifications on a specific device
     */
    private suspend fun getOriginalStock(productId: Long, deviceId: String): Double {
        // In a real implementation, you'd need to track stock changes per device
        // This is a placeholder - you'll need a separate table to track baseline values
        return 0.0
    }

    /**
     * Processes a batch of remote data, resolving conflicts with local data
     */
    suspend fun processBatchSync(remoteData: List<SyncableEntity>) {
        remoteData.forEach { entity ->
            when (entity) {
                is CustomerDetail -> {
                    val localData = database.customerDetailDao().getCustomerById(entity.customerId)
                    val resolved = resolveConflict(localData, entity) as CustomerDetail
                    if (resolved.isDeleted) {
                        database.customerDetailDao().markAsDeleted(resolved.customerId)
                    } else {
                        database.customerDetailDao().insertCustomer(resolved)
                    }
                }
                is CustomerLocation -> {
                    val localData = database.customerLocationDao().getLocationById(entity.locationId)
                    val resolved = resolveConflict(localData, entity) as CustomerLocation
                    if (resolved.isDeleted) {
                        database.customerLocationDao().markAsDeleted(resolved.locationId)
                    } else {
                        database.customerLocationDao().insertLocation(resolved)
                    }
                }
                is Products -> {
                    val localData = database.productsDao().getProductById(entity.productId)
                    val resolved = resolveConflict(localData, entity) as Products
                    if (resolved.isDeleted) {
                        database.productsDao().markAsDeleted(resolved.productId)
                    } else {
                        database.productsDao().insertProduct(resolved)
                    }
                }
                is EmployeeDetail -> {
                    val localData = database.employeeDetailDao().getEmployeeById(entity.employeeId)
                    val resolved = resolveConflict(localData, entity) as EmployeeDetail
                    if (resolved.isDeleted) {
                        database.employeeDetailDao().markAsDeleted(resolved.employeeId)
                    } else {
                        database.employeeDetailDao().insertEmployee(resolved)
                    }
                }
                is SalesItem -> {
                    val localData = database.salesItemDao().getSalesById(entity.salesId)
                    val resolved = resolveConflict(localData, entity) as SalesItem
                    if (resolved.isDeleted) {
                        database.salesItemDao().markAsDeleted(resolved.salesId)
                    } else {
                        database.salesItemDao().insertSalesItem(resolved)
                    }
                }
            }
        }
    }
}