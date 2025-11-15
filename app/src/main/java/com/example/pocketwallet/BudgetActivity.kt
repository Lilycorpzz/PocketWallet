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
