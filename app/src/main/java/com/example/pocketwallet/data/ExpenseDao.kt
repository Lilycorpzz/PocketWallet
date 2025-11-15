package com.example.pocketwallet.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses")
    suspend fun getAll(): List<ExpenseEntity>

    // Get expenses inside a month
    @Query("SELECT * FROM expenses WHERE timestamp BETWEEN :start AND :end")
    suspend fun getExpensesInRange(start: Long, end: Long): List<ExpenseEntity>
}