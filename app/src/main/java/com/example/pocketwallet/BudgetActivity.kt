package com.example.pocketwallet

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BudgetActivity : AppCompatActivity() {

    private lateinit var inputMinGoal: EditText
    private lateinit var inputMaxGoal: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var budgetSummary: TextView
    private lateinit var appLogo: ImageView
    private lateinit var prefs: SharedPreferences

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

        // Load saved values (if any)
        val savedMin = prefs.getFloat("minGoal", -1f)
        val savedMax = prefs.getFloat("maxGoal", -1f)
        if (savedMin >= 0f) inputMinGoal.setText(savedMin.toString())
        if (savedMax >= 0f) inputMaxGoal.setText(savedMax.toString())
        if (savedMin >= 0f && savedMax >= 0f) updateSummary(savedMin.toDouble(), savedMax.toDouble())

        saveButton.setOnClickListener {
            val min = inputMinGoal.text.toString().toDoubleOrNull()
            val max = inputMaxGoal.text.toString().toDoubleOrNull()

            if (min == null || max == null) {
                Toast.makeText(this, "Please enter both goals in Rands.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (min >= max) {
                Toast.makeText(this, "Minimum must be less than maximum.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to SharedPreferences
            prefs.edit()
                .putFloat("minGoal", min.toFloat())
                .putFloat("maxGoal", max.toFloat())
                .apply()

            updateSummary(min, max)
            Toast.makeText(this, "Budget goals saved!", Toast.LENGTH_SHORT).show()
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
}
