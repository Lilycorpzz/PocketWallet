package com.example.pocketwallet

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val username: String,
    val passwordHash: String
)

/*@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val name: String,
    val description: String,
    val color: Int,
    val budget: Double
)*/

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(User::class, ["id"], ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Category::class, ["id"], ["categoryId"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val categoryId: Long?,
    val name: String,
    val description: String?,
    val type: String, // "Income" or "Expense"
    val amount: Double,
    val date: Long, // store as millis
    val photoUri: String? = null
)

@Entity(tableName = "budget_goals")
data class BudgetGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val minMonthly: Double,
    val maxMonthly: Double,
    val createdAt: Long = System.currentTimeMillis()
)

