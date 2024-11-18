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
        SalesItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDetailDao(): CustomerDetailDao
    abstract fun employeeDetailDao(): EmployeeDetailDao
    abstract fun productsDao(): ProductsDao
    abstract fun salesItemDao(): SalesItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kaydens_digital_assistant_db"
                )
                    .fallbackToDestructiveMigration() // Use with caution in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}