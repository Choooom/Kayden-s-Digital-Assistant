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
    version = 5,
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

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE sales_items ADD COLUMN deposit REAL NOT NULL DEFAULT 0.0"
                )
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
                    .addMigrations(MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}