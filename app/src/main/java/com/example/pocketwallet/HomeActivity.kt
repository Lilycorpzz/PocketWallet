package com.example.pocketwallet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.example.pocketwallet.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



class HomeActivity : AppCompatActivity() {

    // --- Buttons ---
    private lateinit var historyButton: ImageButton
    private lateinit var categoriesButton: ImageButton
    private lateinit var addExpenseButton: ImageButton
    private lateinit var dashboardButton: ImageButton

    // --- Views ---
    private lateinit var balanceAmount: TextView
    private lateinit var categoryFood: TextView
    private lateinit var categoryTransport: TextView
    private lateinit var categoryHousing: TextView
    private lateinit var categoryLeisure: TextView
    private lateinit var budgetWarning: TextView

    // --- Database & prefs ---
    private lateinit var db: AppDatabase
    private lateinit var prefs: SharedPreferences

    companion object {
        private const val TAG = "HomeActivity"
        private const val CHANNEL_ID = "budget_channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        db = AppDatabase.getDatabase(this)
        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        // Buttons
        historyButton = findViewById(R.id.button_history)
        categoriesButton = findViewById(R.id.button_categories)
        addExpenseButton = findViewById(R.id.button_add_expense)
        dashboardButton = findViewById(R.id.button_dashboard)

        // Views (fixing incorrect bindings from before)
        balanceAmount = findViewById(R.id.text_balance_amount)
        categoryFood = findViewById(R.id.text1)
        categoryTransport = findViewById(R.id.text2)
        categoryHousing = findViewById(R.id.text3)
        categoryLeisure = findViewById(R.id.text4)
        budgetWarning = findViewById(R.id.textView2) // this is the warning TextView in your layout

        createNotificationChannel()
        loadExpenseData()

        historyButton.setOnClickListener {
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "budget_channel",
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

    }

    private fun loadExpenseData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val expenses = db.expenseDao().getAll()

                // Total income & expenses
                val totalIncome = expenses.filter { it.type == "Income" }.sumOf { it.value }
                val totalExpenses = expenses.filter { it.type == "Expense" }.sumOf { it.value }
                val balance = totalIncome - totalExpenses

                // Group by category for the bottom list
                val totalsByCategory = expenses
                    .filter { it.type == "Expense" }
                    .groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.value } }

                withContext(Dispatchers.Main) {
                    // Update UI
                    balanceAmount.text = "R %.2f".format(balance)

                    categoryFood.text = "Food: -R %.2f".format(totalsByCategory["Food"] ?: 0.0)
                    categoryTransport.text = "Transport: -R %.2f".format(totalsByCategory["Transport"] ?: 0.0)
                    categoryHousing.text = "Housing: -R %.2f".format(totalsByCategory["Housing"] ?: 0.0)
                    categoryLeisure.text = "Leisure: -R %.2f".format(totalsByCategory["Leisure"] ?: 0.0)

                    // IMPORTANT: check budgets against TOTAL SPENT, not balance
                    checkBudgetLimits(totalExpenses)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@HomeActivity,
                        "Error loading data: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e(TAG, "loadExpenseData error", e)
                }
            }
        }
    }

    /**
     * Check budget limits comparing totalSpent (sum of expenses) to min and max goals.
     * totalSpent: total spent this (all) months — to be more precise you can change DAO to return monthly sums.
     */
    private fun checkBudgetLimits(totalSpent: Double) {
        val min = prefs.getFloat("minGoal", -1f).toDouble()
        val max = prefs.getFloat("maxGoal", -1f).toDouble()

        if (min < 0 || max < 0) {
            budgetWarning.text = ""
            return
        }

        // Debug logs
        Log.d(TAG, "checkBudgetLimits -> totalSpent: $totalSpent, min: $min, max: $max")

        // Use totalSpent to decide messages
        when {
            totalSpent < min -> {
                budgetWarning.text = "⚠ You are BELOW your minimum budget!"
                Toast.makeText(this, "Warning: You are below your minimum budget!", Toast.LENGTH_LONG).show()
            }

            totalSpent > max -> {
                budgetWarning.text = "⚠ You EXCEEDED your maximum budget!"
                Toast.makeText(this, "Warning: You exceeded your maximum budget!", Toast.LENGTH_LONG).show()
            }

            else -> {
                budgetWarning.text = "✓ You are within your budget range."
            }
        }

        // Also send threshold notifications for spending limit (80% and 100%)
        if (max > 0) {
            val pct = (totalSpent / max)
            if (pct >= 1.0) {
                sendSpendingNotification("You have reached your spending limit for the month.")
            } else if (pct >= 0.8) {
                sendSpendingNotification("You have used ${ (pct*100).toInt() }% of your monthly budget.")
            }
        }
    }

    /* ---------- Notifications ---------- */

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Budget Alerts"
            val desc = "Alerts for reaching monthly budget thresholds"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply { description = desc }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

    private fun sendSpendingNotification(message: String) {
        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Spending Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
