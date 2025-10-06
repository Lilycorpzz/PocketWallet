package com.example.pocketwallet
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pocketwallet.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    // --- Buttons ---
    private lateinit var historyButton: ImageButton
    private lateinit var categoriesButton: ImageButton
    private lateinit var addExpenseButton: ImageButton
    private lateinit var dashboardButton: ImageButton

    // --- TextViews for displaying data ---
    private lateinit var balanceAmount: TextView
    private lateinit var incomeAmount: TextView
    private lateinit var expenseAmount: TextView
    private lateinit var categoryFood: TextView
    private lateinit var categoryTransport: TextView
    private lateinit var categoryHousing: TextView
    private lateinit var categoryLeisure: TextView

    // --- Database reference ---
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // Initialize database
        db = AppDatabase.getDatabase(this)

        // Initialize buttons
        historyButton = findViewById(R.id.button_history)
        categoriesButton = findViewById(R.id.button_categories)
        addExpenseButton = findViewById(R.id.button_add_expense)
        dashboardButton = findViewById(R.id.button_dashboard)

        // Initialize text views
        balanceAmount = findViewById(R.id.text_balance_amount)
        incomeAmount = findViewById(R.id.text_balance_label) // optional for clarity, not used here
        expenseAmount = findViewById(R.id.text_balance_amount) // optional
        categoryFood = findViewById(R.id.text1) // will update below
        categoryTransport = findViewById(R.id.text2)
        categoryHousing = findViewById(R.id.text3)
        categoryLeisure = findViewById(R.id.text4)

        // 🧠 Load saved expense data
        loadExpenseData()

        // --- Button click listeners ---
        historyButton.setOnClickListener {
            Toast.makeText(this, "Achievements clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AchievementsActivity::class.java))
        }

        categoriesButton.setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }

        addExpenseButton.setOnClickListener {
            startActivity(Intent(this, RegisterExpenseActivity::class.java))
        }

        dashboardButton.setOnClickListener {
            startActivity(Intent(this, BudgetActivity::class.java))
        }
    }

    // ------------------------------------------------------------
    // 🔽 Function: Load all saved expense data from Room Database
    // ------------------------------------------------------------
    private fun loadExpenseData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val expenses = db.expenseDao().getAll()

                // Separate income and expenses
                val totalIncome = expenses.filter { it.type == "Income" }.sumOf { it.value }
                val totalExpenses = expenses.filter { it.type == "Expense" }.sumOf { it.value }
                val balance = totalIncome - totalExpenses

                // Group by category for the bottom list
                val totalsByCategory = expenses
                    .filter { it.type == "Expense" }
                    .groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.value } }

                withContext(Dispatchers.Main) {
                    // 💰 Update the top card
                    balanceAmount.text = "R %.2f".format(balance)

                    // 🏷️ Update expense categories if they exist
                    categoryFood.text =
                        "Food: -R %.2f".format(totalsByCategory["Food"] ?: 0.0)
                    categoryTransport.text =
                        "Transport: -R %.2f".format(totalsByCategory["Transport"] ?: 0.0)
                    categoryHousing.text =
                        "Housing: -R %.2f".format(totalsByCategory["Housing"] ?: 0.0)
                    categoryLeisure.text =
                        "Leisure: -R %.2f".format(totalsByCategory["Leisure"] ?: 0.0)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@HomeActivity,
                        "Error loading data: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}