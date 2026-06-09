package com.example.paanee.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HydrationDao {
    @Insert
    suspend fun insertLog(log: HydrationLog)

    @Query("SELECT * FROM hydration_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<HydrationLog>>

    @Query("SELECT SUM(amountMl) FROM hydration_logs WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay")
    fun getTotalIntakeForDay(startOfDay: Long, endOfDay: Long): Flow<Int?>
    
    @Query("SELECT * FROM hydration_logs WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay ORDER BY timestamp DESC")
    fun getLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<HydrationLog>>
}
