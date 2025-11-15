package com.example.pocketwallet

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// 🔥 NEW IMPORTS FOR CHART
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class BudgetActivity : AppCompatActivity() {

    private lateinit var inputMinGoal: EditText
    private lateinit var inputMaxGoal: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var budgetSummary: TextView
    private lateinit var appLogo: ImageView
    private lateinit var prefs: SharedPreferences

    //   Chart reference
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budget)

        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        inputMinGoal = findViewById(R.id.inputMinGoal)
        inputMaxGoal = findViewById(R.id.inputMaxGoal)
        saveButton = findViewById(R.id.buttonSaveBudget)
        backButton = findViewById(R.id.backButton2)
        budgetSummary = findViewById(R.id.budgetSummary2)
        appLogo = findViewById(R.id.appLogo)

        //  Find the chart in the layout
        barChart = findViewById(R.id.budgetBarChart)

        // Load saved values (if any)
        val savedMin = prefs.getFloat("minGoal", -1f)
        val savedMax = prefs.getFloat("maxGoal", -1f)
        if (savedMin >= 0f) inputMinGoal.setText(savedMin.toString())
        if (savedMax >= 0f) inputMaxGoal.setText(savedMax.toString())
        if (savedMin >= 0f && savedMax >= 0f) {
            updateSummary(savedMin.toDouble(), savedMax.toDouble())
            updateChart(savedMin.toDouble(), savedMax.toDouble())   //   Load chart
        }

        saveButton.setOnClickListener {
            val min = inputMinGoal.text.toString().trim()
            val max = inputMaxGoal.text.toString().trim()

            if (min.isEmpty() || max.isEmpty()) {
                Toast.makeText(this, "Enter both min and max goals", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseManager.db.child("goals").setValue(
                mapOf(
                    "minGoal" to min.toDouble(),
                    "maxGoal" to max.toDouble()
                )
            ).addOnSuccessListener {
                Toast.makeText(this, "Goals saved!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error saving goals", Toast.LENGTH_SHORT).show()
            }

            prefs.edit().putFloat("minGoal", min.toFloat()).apply()
            prefs.edit().putFloat("maxGoal", max.toFloat()).apply()

            //  Update chart + summary after saving
            updateSummary(min.toDouble(), max.toDouble())
            updateChart(min.toDouble(), max.toDouble())
        }

        backButton.setOnClickListener {
            // simply finish the activity to go back to previous screen
            finish()
        }
    }

    private fun updateSummary(min: Double, max: Double) {
        val summary = "Minimum: R %.2f\nMaximum: R %.2f".format(min, max)
        budgetSummary.text = summary
    }

    //  Function to update chart
    private fun updateChart(min: Double, max: Double) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, min.toFloat()))
        entries.add(BarEntry(1f, max.toFloat()))

        val dataSet = BarDataSet(entries, "Budget Goals")
        val barData = BarData(dataSet)

        barChart.data = barData
        barChart.setFitBars(true)

        val desc = Description()
        desc.text = "Min vs Max Budget"
        barChart.description = desc

        barChart.invalidate() // refresh chart
    }
}
