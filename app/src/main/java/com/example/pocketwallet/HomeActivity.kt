package com.example.pocketwallet
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pocketwallet.data.AppDatabase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        barChart = findViewById(R.id.barChart)
        db = AppDatabase.getDatabase(this)

        setupChart()
    }

    private fun setupChart() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val expenses = db.expenseDao().getAll()
                val categories = db.categoryDao().getAll()

                // Map category names to total expenses
                val totalsByCategory = categories.associate { cat ->
                    val total = expenses
                        .filter { it.category == cat.name && it.type == "Expense" }
                        .sumOf { it.value }
                    cat.name to total
                }

                // Get min and max budgets
                val minGoal = categories.minOfOrNull { it.budget } ?: 0.0
                val maxGoal = categories.maxOfOrNull { it.budget } ?: 0.0

                // Build BarEntries
                val entries = totalsByCategory.entries.mapIndexed { index, entry ->
                    BarEntry(index.toFloat(), entry.value.toFloat())
                }

                val dataSet = BarDataSet(entries, "Expenses")
                dataSet.colors = categories.map { it.color }
                dataSet.valueTextColor = Color.BLACK
                dataSet.valueTextSize = 12f

                val barData = BarData(dataSet)
                barData.barWidth = 0.6f

                withContext(Dispatchers.Main) {
                    barChart.data = barData

                    // X-axis labels
                    val xAxis = barChart.xAxis
                    xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                        totalsByCategory.keys.toList()
                    )
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                    xAxis.textSize = 12f

                    // Y-axis
                    val leftAxis = barChart.axisLeft
                    leftAxis.axisMinimum = 0f

                    // Add limit lines for min and max goals
                    if (minGoal > 0) {
                        val minLine = LimitLine(minGoal.toFloat(), "Min Goal")
                        minLine.lineColor = Color.GREEN
                        minLine.lineWidth = 2f
                        leftAxis.addLimitLine(minLine)
                    }
                    if (maxGoal > 0) {
                        val maxLine = LimitLine(maxGoal.toFloat(), "Max Goal")
                        maxLine.lineColor = Color.RED
                        maxLine.lineWidth = 2f
                        leftAxis.addLimitLine(maxLine)
                    }

                    barChart.axisRight.isEnabled = false
                    barChart.description = Description().apply { text = "" }
                    barChart.legend.isEnabled = true
                    barChart.invalidate()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Error loading chart: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
