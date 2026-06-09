package com.example.paanee.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HydrationLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hydrationDao(): HydrationDao
}
