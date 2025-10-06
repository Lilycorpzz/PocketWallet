package com.example.pocketwallet

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class HomeActivity : AppCompatActivity() {

    private lateinit var historyButton: ImageButton
    private lateinit var categoriesButton: ImageButton
    private lateinit var addExpenseButton: ImageButton
    private lateinit var dashboardButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // Initialize buttons
        historyButton = findViewById(R.id.button_history)
        categoriesButton = findViewById(R.id.button_categories)
        addExpenseButton = findViewById(R.id.button_add_expense)
        dashboardButton = findViewById(R.id.button_dashboard)

        // Button click listeners
        historyButton.setOnClickListener {
            Toast.makeText(this, "Acheivements clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to HistoryActivity if you have one
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }

        categoriesButton.setOnClickListener {
            val intent = Intent(this, CategoriesActivity::class.java)
            startActivity(intent)
        }

        addExpenseButton.setOnClickListener {
            Toast.makeText(this, "Add Expense clicked", Toast.LENGTH_SHORT).show()
            // Navigate to RegisterExpenseActivity
            val intent = Intent(this, RegisterExpenseActivity::class.java)
            startActivity(intent)
        }

        dashboardButton.setOnClickListener {
            val intent = Intent(this, BudgetActivity::class.java)
            startActivity(intent)
        }



    }
}
