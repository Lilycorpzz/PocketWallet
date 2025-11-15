package com.example.pocketwallet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val name: String,
    val description: String,
    val category: String,

    val value: Double,
    val type: String,

    // NEW FIELDS FOR DATE & TIME
    val dateString: String,       // "2025-08-05"
    val startTime: String,        // "14:30"
    val endTime: String,          // "16:00"

    // For filtering by month/week/custom range
    val timestamp: Long,

    val photoUri: String? = null
)