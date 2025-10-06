package com.example.pocketwallet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense): Long
    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :from AND :to") suspend fun getForPeriod(userId: Long, from: Long, to: Long): List<Expense>

    @Query("""
        SELECT c.name AS categoryName, SUM(CASE WHEN e.type = 'Expense' THEN e.amount ELSE -e.amount END) AS total
        FROM expenses e
        LEFT JOIN categories c ON e.categoryId = c.id
        WHERE e.userId = :userId AND e.date BETWEEN :from AND :to
        GROUP BY e.categoryId
    """)
    suspend fun getTotalsByCategory(userId: Long, from: Long, to: Long): List<CategoryTotal>
}

data class CategoryTotal(val categoryName: String?, val total: Double)
