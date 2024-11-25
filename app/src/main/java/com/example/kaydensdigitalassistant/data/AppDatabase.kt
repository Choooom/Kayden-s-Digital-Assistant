package com.example.kaydensdigitalassistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CustomerDetail::class,
        EmployeeDetail::class,
        Products::class,
        SalesItem::class,
        CustomerLocation::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDetailDao(): CustomerDetailDao
    abstract fun employeeDetailDao(): EmployeeDetailDao
    abstract fun productsDao(): ProductsDao
    abstract fun salesItemDao(): SalesItemDao
    abstract fun appDao(): AppDao
    abstract fun customerLocationDao(): CustomerLocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE sales_items ADD COLUMN paymentMethod TEXT NOT NULL DEFAULT 'Cash'"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Drop the table if it exists
                database.execSQL("DROP TABLE IF EXISTS customer_locations")

                // Create the table with the exact expected structure
                database.execSQL("""
            CREATE TABLE customer_locations (
                locationId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                customerId INTEGER NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                FOREIGN KEY (customerId) REFERENCES customer_details(customerId)
            )
        """)
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AppDatabase"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}