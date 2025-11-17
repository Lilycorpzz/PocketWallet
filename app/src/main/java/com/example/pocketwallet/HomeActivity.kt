package com.example.pocketwallet

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.pocketwallet.data.AppDatabase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    // Buttons
    private lateinit var historyButton: ImageButton
    private lateinit var categoriesButton: ImageButton
    private lateinit var addExpenseButton: ImageButton
    private lateinit var dashboardButton: ImageButton

    // Views
    private lateinit var balanceAmount: TextView
    private lateinit var categoryFood: TextView
    private lateinit var categoryTransport: TextView
    private lateinit var categoryHousing: TextView
    private lateinit var categoryLeisure: TextView
    private lateinit var budgetWarning: TextView
    private lateinit var recentEntry: TextView
    private lateinit var barChart: BarChart

    // Database & Prefs
    private lateinit var db: AppDatabase
    private lateinit var prefs: SharedPreferences

    private val categoryNames = ArrayList<String>()

    companion object {
        private const val TAG = "HomeActivity"
        private const val CHANNEL_ID = "budget_channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        db = AppDatabase.getDatabase(this)
        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        // UI Binds
        barChart = findViewById(R.id.categoryBarChart)
        historyButton = findViewById(R.id.button_history)
        categoriesButton = findViewById(R.id.button_categories)
        addExpenseButton = findViewById(R.id.button_add_expense)
        dashboardButton = findViewById(R.id.button_dashboard)

        balanceAmount = findViewById(R.id.text_balance_amount)
        categoryFood = findViewById(R.id.text1)
        categoryTransport = findViewById(R.id.text2)
        categoryHousing = findViewById(R.id.text3)
        categoryLeisure = findViewById(R.id.text4)
        budgetWarning = findViewById(R.id.textView2)
        recentEntry = findViewById(R.id.textRecentEntry)

        createNotificationChannel()
        loadExpenseData()
        loadCategoriesFromFirebase()

        // Click Listeners
        historyButton.setOnClickListener { startActivity(Intent(this, AchievementsActivity::class.java)) }
        categoriesButton.setOnClickListener { startActivity(Intent(this, CategoriesActivity::class.java)) }
        addExpenseButton.setOnClickListener { startActivity(Intent(this, RegisterExpenseActivity::class.java)) }
        dashboardButton.setOnClickListener { startActivity(Intent(this, BudgetActivity::class.java)) }
    }


    /* ------------------------------------------------------------------------
       FIREBASE CATEGORY LOADING
    ------------------------------------------------------------------------ */

    private fun loadCategoriesFromFirebase() {
        FirebaseManager.db.child("categories").get()
            .addOnSuccessListener { snapshot ->

                val categoryEntries = ArrayList<BarEntry>()
                categoryNames.clear()
                var index = 0f

                snapshot.children.forEach { child ->
                    val name = child.child("name").getValue(String::class.java) ?: "Unknown"
                    val budget = child.child("budget").getValue(Double::class.java) ?: 0.0

                    categoryEntries.add(BarEntry(index, budget.toFloat()))
                    categoryNames.add(name)
                    index++
                }

                loadGoalsFromFirebase(categoryEntries)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadGoalsFromFirebase(categoryEntries: ArrayList<BarEntry>) {
        FirebaseManager.db.child("goals").get()
            .addOnSuccessListener { snap ->
                val min = snap.child("minGoal").getValue(Double::class.java) ?: 0.0
                val max = snap.child("maxGoal").getValue(Double::class.java) ?: 0.0

                val goalEntries = arrayListOf(
                    BarEntry(0f, min.toFloat()),
                    BarEntry(1f, max.toFloat())
                )

                displayGraph(categoryEntries, goalEntries)
            }
            .addOnFailureListener {
                displayGraph(categoryEntries, ArrayList())
            }
    }

    private fun displayGraph(categoryEntries: ArrayList<BarEntry>, goalEntries: ArrayList<BarEntry>) {
        val categorySet = BarDataSet(categoryEntries, "Category Budgets")
        categorySet.color = Color.parseColor("#4CAF50")

        val goalSet = BarDataSet(goalEntries, "Min/Max Goals")
        goalSet.color = Color.parseColor("#F44336")

        val data = BarData(categorySet, goalSet)
        data.barWidth = 0.35f

        barChart.data = data
        barChart.setFitBars(true)

        val desc = Description()
        desc.text = "Category Spending vs Goals"
        barChart.description = desc

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(categoryNames)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f

        barChart.axisRight.isEnabled = false
        barChart.invalidate()
    }


    /* ------------------------------------------------------------------------
       LOAD EXPENSE DATA (BALANCE + RECENT ENTRY)
    ------------------------------------------------------------------------ */

    private fun loadExpenseData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val expenses = db.expenseDao().getAll()

                val totalIncome = expenses.filter { it.type == "Income" }.sumOf { it.value }
                val totalExpenses = expenses.filter { it.type == "Expense" }.sumOf { it.value }
                val balance = totalIncome - totalExpenses

                // Most recent entry (id is Int so safe)
                val lastEntry = expenses.maxByOrNull { it.id }

                // Totals per category
                val totalsByCategory = expenses
                    .filter { it.type == "Expense" }
                    .groupBy { it.category }
                    .mapValues { it.value.sumOf { item -> item.value } }

                withContext(Dispatchers.Main) {

                    // BALANCE
                    balanceAmount.text = "R %.2f".format(balance)

                    // CATEGORY TOTALS
                    categoryFood.text = "Food: -R %.2f".format(totalsByCategory["Food"] ?: 0.0)
                    categoryTransport.text = "Transport: -R %.2f".format(totalsByCategory["Transport"] ?: 0.0)
                    categoryHousing.text = "Housing: -R %.2f".format(totalsByCategory["Housing"] ?: 0.0)
                    categoryLeisure.text = "Leisure: -R %.2f".format(totalsByCategory["Leisure"] ?: 0.0)

                    // RECENT ENTRY
                    recentEntry.text = if (lastEntry != null) {
                        "${lastEntry.type}: ${lastEntry.name} — R %.2f".format(lastEntry.value)
                    } else {
                        "Recent Entry: None"
                    }

                    checkBudgetLimits(totalExpenses)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Error loading data: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "loadExpenseData error", e)
                }
            }
        }
    }


    /* ------------------------------------------------------------------------
       BUDGET LOGIC
    ------------------------------------------------------------------------ */

    private fun checkBudgetLimits(totalSpent: Double) {
        val min = prefs.getFloat("minGoal", -1f).toDouble()
        val max = prefs.getFloat("maxGoal", -1f).toDouble()

        when {
            min < 0 || max < 0 -> budgetWarning.text = ""

            totalSpent < min -> {
                budgetWarning.text = "⚠ You are BELOW your minimum budget!"
            }

            totalSpent > max -> {
                budgetWarning.text = "⚠ You EXCEEDED your maximum budget!"
            }

            else -> {
                budgetWarning.text = "✓ You are within your budget range."
            }
        }

        if (max > 0) {
            val pct = totalSpent / max
            when {
                pct >= 1.0 -> sendSpendingNotification("You have reached your spending limit.")
                pct >= 0.8 -> sendSpendingNotification("You have used ${(pct * 100).toInt()}% of your budget.")
            }
        }
    }


    /* ------------------------------------------------------------------------
       NOTIFICATIONS
    ------------------------------------------------------------------------ */

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Budget Alerts", NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private val NOTIFICATION_PERMISSION_REQUEST = 99

    private fun sendSpendingNotification(message: String) {

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST
            )
            return
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Spending Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
