package com.hesabi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val instanceResult = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hesabi_database"
                ).build()
                instance = instanceResult
                instanceResult
            }
        }
    }
}
