package com.example.pocketwallet.data

data class Expense(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val photoUrl: String? = null
)
