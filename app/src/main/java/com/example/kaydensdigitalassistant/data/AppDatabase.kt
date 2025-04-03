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
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDetailDao(): CustomerDetailDao
    abstract fun employeeDetailDao(): EmployeeDetailDao
    abstract fun productsDao(): ProductsDao
    abstract fun salesItemDao(): SalesItemDao
    abstract fun customerLocationDao(): CustomerLocationDao
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // For customer_details
                //database.execSQL("ALTER TABLE customer_details ADD COLUMN address TEXT NOT NULL DEFAULT ''")
                //database.execSQL("ALTER TABLE customer_details ADD COLUMN preferredOrder TEXT NOT NULL DEFAULT '[]'")
                //database.execSQL("ALTER TABLE customer_details ADD COLUMN registrationDate TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE customer_details ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE customer_details ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE customer_details ADD COLUMN deviceId TEXT NOT NULL DEFAULT ''")

                // For employee_details
                //database.execSQL("ALTER TABLE employee_details ADD COLUMN birthdate TEXT NOT NULL DEFAULT ''")
                //database.execSQL("ALTER TABLE employee_details ADD COLUMN password TEXT NOT NULL DEFAULT ''")
                //database.execSQL("ALTER TABLE employee_details ADD COLUMN username TEXT NOT NULL DEFAULT ''")
                //database.execSQL("ALTER TABLE employee_details ADD COLUMN userProfile BLOB")
                database.execSQL("ALTER TABLE employee_details ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE employee_details ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE employee_details ADD COLUMN deviceId TEXT NOT NULL DEFAULT ''")

                // For products
                //database.execSQL("ALTER TABLE products ADD COLUMN productIcon BLOB")
                //database.execSQL("ALTER TABLE products ADD COLUMN isHalfable INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE products ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE products ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE products ADD COLUMN deviceId TEXT NOT NULL DEFAULT ''")

                // For sales_items
                //database.execSQL("ALTER TABLE sales_items ADD COLUMN orderDetails TEXT NOT NULL DEFAULT '[]'")
                //database.execSQL("ALTER TABLE sales_items ADD COLUMN deposit REAL NOT NULL DEFAULT 0.0")
                //database.execSQL("ALTER TABLE sales_items ADD COLUMN referenceNumber TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE sales_items ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE sales_items ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE sales_items ADD COLUMN deviceId TEXT NOT NULL DEFAULT ''")

                // For customer_locations
                database.execSQL("ALTER TABLE customer_locations ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE customer_locations ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE customer_locations ADD COLUMN deviceId TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AppDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_5_6)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}