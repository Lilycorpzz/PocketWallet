package com.example.pocketwallet

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class CategoryGraphActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private val categoryNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_graph) // change to your correct layout

        barChart = findViewById(R.id.categoryBarChart)

        loadCategoriesFromFirebase()
    }

    // 🔥 STEP 1 — Load categories first
    private fun loadCategoriesFromFirebase() {
        FirebaseManager.db.child("categories").get()
            .addOnSuccessListener { snapshot ->

                val categoryEntries = ArrayList<BarEntry>()
                categoryNames.clear()
                var index = 0f

                if (!snapshot.exists()) {
                    Toast.makeText(this, "No categories found in Firebase", Toast.LENGTH_SHORT).show()
                } else {
                    snapshot.children.forEach { child ->
                        val name = child.child("name").getValue(String::class.java) ?: "Unknown"
                        val budget = child.child("budget").getValue(Double::class.java) ?: 0.0

                        categoryEntries.add(BarEntry(index, budget.toFloat()))
                        categoryNames.add(name)
                        index += 1f
                    }
                }

                loadGoalsFromFirebase(categoryEntries)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔥 STEP 2 — Load min/max goals from Firebase
    private fun loadGoalsFromFirebase(categoryEntries: ArrayList<BarEntry>) {
        FirebaseManager.db.child("goals").get()
            .addOnSuccessListener { snap ->
                val min = snap.child("minGoal").getValue(Double::class.java) ?: 0.0
                val max = snap.child("maxGoal").getValue(Double::class.java) ?: 0.0

                val goalEntries = ArrayList<BarEntry>()
                goalEntries.add(BarEntry(0f, min.toFloat()))
                goalEntries.add(BarEntry(1f, max.toFloat()))

                displayGraph(categoryEntries, goalEntries)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load goals", Toast.LENGTH_SHORT).show()
                displayGraph(categoryEntries, ArrayList()) // still show categories
            }
    }

    // 🔥 STEP 3 — Display everything in the chart
    private fun displayGraph(
        categoryEntries: ArrayList<BarEntry>,
        goalEntries: ArrayList<BarEntry>
    ) {
        // Category bars
        val categorySet = BarDataSet(categoryEntries, "Category Budgets")
        categorySet.color = Color.parseColor("#4CAF50") // green

        // Goal bars
        val goalSet = BarDataSet(goalEntries, "Min/Max Goals")
        goalSet.color = Color.parseColor("#F44336") // red

        val data = BarData(categorySet, goalSet)
        data.barWidth = 0.35f

        barChart.data = data
        barChart.setFitBars(true)

        val desc = Description()
        desc.text = "Category Spending vs Goals"
        barChart.description = desc

        // X-axis labels (category names)
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(categoryNames)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f

        barChart.axisRight.isEnabled = false
        barChart.invalidate()
    }
}
