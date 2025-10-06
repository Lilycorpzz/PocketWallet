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
    val type: String, // 👈 this must exist since you use it.type
    val photoUri: String? = null
)