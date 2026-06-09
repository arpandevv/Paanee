package com.example.paanee.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hydration_logs")
data class HydrationLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amountMl: Int,
    val drinkType: String, // e.g., "Water", "Coffee", "Juice"
    val timestamp: Long
)
